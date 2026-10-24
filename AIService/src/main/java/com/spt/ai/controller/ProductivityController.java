package com.spt.ai.controller;

import com.spt.ai.dto.AnalyticsResponse;
import com.spt.ai.dto.SuggestionResponse;
import com.spt.ai.service.AnalyticsService;
import com.spt.ai.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ProductivityController {

    private final AnalyticsService analyticsService;
    private final SuggestionService suggestionService;

    @GetMapping("/analytics/weekly")
    public AnalyticsResponse weekly(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week) {
        LocalDate today = LocalDate.now();
        WeekFields fields = WeekFields.ISO;
        int selectedYear = year == null ? today.get(fields.weekBasedYear()) : year;
        int selectedWeek = week == null ? today.get(fields.weekOfWeekBasedYear()) : week;
        return analyticsService.getWeekly(userId, selectedYear, selectedWeek);
    }

    @GetMapping("/analytics/monthly")
    public AnalyticsResponse monthly(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate today = LocalDate.now();
        return analyticsService.getMonthly(userId,
                year == null ? today.getYear() : year,
                month == null ? today.getMonthValue() : month);
    }

    @GetMapping("/suggestions")
    public List<SuggestionResponse> suggestions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return suggestionService.getRecent(userId, limit);
    }

    @PostMapping("/suggestions/generate")
    public ResponseEntity<SuggestionResponse> generate(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(suggestionService.generate(userId));
    }

    @PatchMapping("/suggestions/{suggestionId}/read")
    public SuggestionResponse markRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long suggestionId) {
        return suggestionService.markRead(userId, suggestionId);
    }
}

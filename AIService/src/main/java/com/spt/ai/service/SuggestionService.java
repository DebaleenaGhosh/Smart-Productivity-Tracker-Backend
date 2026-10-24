package com.spt.ai.service;

import com.spt.ai.dto.AnalyticsResponse;
import com.spt.ai.dto.SuggestionResponse;
import com.spt.ai.entity.Suggestion;
import com.spt.ai.repository.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final AnalyticsService analyticsService;
    private final GenerativeAiClient aiClient;

    @Value("${productivity.suggestions.max-history:20}")
    private int maxHistory;

    @Transactional
    public SuggestionResponse generate(Long userId) {
        AnalyticsResponse metrics = analyticsService.getCurrentWeek(userId);
        String fallback = metricBasedMessage(metrics);
        String prompt = "Create one congratulatory or actionable productivity suggestion like which task to focus on"
                + "This week's anonymous metrics are: total tasks=" + metrics.totalTasks()
                + ", completed=" + metrics.completedTasks()
                + ", overdue=" + metrics.overdueTasks()
                + ", completion rate=" + metrics.completionRate() + "%"
                + ", planned minutes=" + metrics.totalEstimatedMinutes()
                + ", actual minutes=" + metrics.totalActualMinutes() + ".";
        String message = aiClient.generate(prompt).orElse(fallback);
        String type = suggestionType(metrics);
        Suggestion suggestion = Suggestion.builder()
                .userId(userId)
                .type(type)
                .title(title(type))
                .message(message)
                .createdAt(LocalDateTime.now())
                .read(false)
                .source(message.equals(fallback) ? "METRICS" : "AI")
                .build();
        return toResponse(suggestionRepository.save(suggestion));
    }

    @Transactional
    public void createCompletionCelebration(Long userId, String taskTitle) {
        String safeTitle = taskTitle == null || taskTitle.isBlank() ? "your task" : "“" + taskTitle + "”";
        Suggestion suggestion = Suggestion.builder()
                .userId(userId)
                .type("CONGRATULATIONS")
                .title("Task completed — great work!")
                .message("You completed " + safeTitle + ". Take a moment to recognize the progress before choosing your next priority.")
                .createdAt(LocalDateTime.now())
                .read(false)
                .source("EVENT")
                .build();
        suggestionRepository.save(suggestion);
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getRecent(Long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, Math.max(1, maxHistory)));
        return suggestionRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SuggestionResponse markRead(Long userId, Long suggestionId) {
        Suggestion suggestion = suggestionRepository.findBySuggestionIdAndUserId(suggestionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
        suggestion.setRead(true);
        return toResponse(suggestionRepository.save(suggestion));
    }

    private String metricBasedMessage(AnalyticsResponse metrics) {
        if (metrics.totalTasks() == 0) {
            return "Plan one small, clearly timed task for this week. A focused first step is easier to start and gives your chart useful data.";
        }
        if (metrics.completionRate() >= 80) {
            return "Excellent consistency — you completed " + metrics.completedTasks() + " of "
                    + metrics.totalTasks() + " tracked tasks this week. Keep the momentum while protecting time for rest.";
        }
        if (metrics.overdueTasks() > 0) {
            return "You have " + metrics.overdueTasks() + " overdue task(s). Pick the smallest one, schedule a 25-minute focus block, and reassess its remaining scope afterward.";
        }
        if (metrics.totalActualMinutes() > metrics.totalEstimatedMinutes() && metrics.totalEstimatedMinutes() > 0) {
            return "Your tasks are taking longer than planned. Add a 20% buffer to similar estimates next week and split larger tasks into smaller checkpoints.";
        }
        return "Your week is taking shape. Reserve a focus block for the highest-priority task and update its actual minutes when complete to improve future recommendations.";
    }

    private String suggestionType(AnalyticsResponse metrics) {
        return metrics.totalTasks() > 0 && metrics.completionRate() >= 80
                ? "CONGRATULATIONS"
                : "IMPROVEMENT";
    }

    private String title(String type) {
        return "CONGRATULATIONS".equals(type) ? "Strong week!" : "Your productivity insight";
    }

    private SuggestionResponse toResponse(Suggestion suggestion) {
        return new SuggestionResponse(suggestion.getSuggestionId(), suggestion.getType(), suggestion.getTitle(),
                suggestion.getMessage(), suggestion.getCreatedAt(), suggestion.isRead(), suggestion.getSource());
    }
}

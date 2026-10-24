package com.spt.ai.service;

import com.spt.ai.dto.AnalyticsResponse;
import com.spt.ai.entity.TaskAnalytics;
import com.spt.ai.repository.TaskAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TaskAnalyticsRepository taskRepository;

    public AnalyticsResponse getWeekly(Long userId, int year, int week) {
        if (week < 1 || week > 53) {
            throw new IllegalArgumentException("Week must be between 1 and 53");
        }
        WeekFields fields = WeekFields.ISO;
        LocalDate start = LocalDate.of(year, 1, 4)
                .with(fields.weekOfWeekBasedYear(), week)
                .with(fields.dayOfWeek(), DayOfWeek.MONDAY.getValue());
        return build(userId, "WEEK", start, start.plusDays(6));
    }

    public AnalyticsResponse getMonthly(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return build(userId, "MONTH", yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public AnalyticsResponse getCurrentWeek(Long userId) {
        LocalDate today = LocalDate.now();
        WeekFields fields = WeekFields.ISO;
        return getWeekly(userId, today.get(fields.weekBasedYear()), today.get(fields.weekOfWeekBasedYear()));
    }

    private AnalyticsResponse build(Long userId, String period, LocalDate start, LocalDate end) {
        List<TaskAnalytics> tasks = taskRepository.findByUserId(userId).stream()
                .filter(task -> belongsToPeriod(task, start, end))
                .toList();

        int completed = (int) tasks.stream().filter(this::isCompleted).count();
        int overdue = (int) tasks.stream().filter(this::isOverdue).count();
        int estimated = tasks.stream()
                .filter(task -> inRange(task.getDueDate(), start, end))
                .mapToInt(this::estimatedMinutes)
                .sum();
        int actual = tasks.stream()
                .filter(task -> task.getCompletedAt() != null && inRange(task.getCompletedAt().toLocalDate(), start, end))
                .mapToInt(this::actualMinutes)
                .sum();
        double completionRate = tasks.isEmpty() ? 0 : round(100.0 * completed / tasks.size());

        Map<String, Integer> categoryAllocation = tasks.stream()
                .filter(task -> inRange(task.getDueDate(), start, end))
                .collect(Collectors.groupingBy(
                        this::category,
                        LinkedHashMap::new,
                        Collectors.summingInt(this::estimatedMinutes)));

        List<AnalyticsResponse.TimeSeriesPoint> timeline = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate currentDate = date;
            int allocatedForDay = sum(tasks, task -> currentDate.equals(task.getDueDate()), this::estimatedMinutes);
            int spentForDay = sum(tasks,
                    task -> task.getCompletedAt() != null && currentDate.equals(task.getCompletedAt().toLocalDate()),
                    this::actualMinutes);
            int completedForDay = (int) tasks.stream()
                    .filter(task -> task.getCompletedAt() != null && currentDate.equals(task.getCompletedAt().toLocalDate()))
                    .count();
            timeline.add(new AnalyticsResponse.TimeSeriesPoint(currentDate, allocatedForDay, spentForDay, completedForDay));
        }

        return new AnalyticsResponse(period, start, end, tasks.size(), completed, overdue,
                completionRate, estimated, actual, timeline, categoryAllocation);
    }

    private int sum(List<TaskAnalytics> tasks, Predicate<TaskAnalytics> filter,
                    java.util.function.ToIntFunction<TaskAnalytics> value) {
        return tasks.stream().filter(filter).mapToInt(value).sum();
    }

    private boolean belongsToPeriod(TaskAnalytics task, LocalDate start, LocalDate end) {
        return inRange(task.getDueDate(), start, end)
                || (task.getCreatedAt() != null && inRange(task.getCreatedAt().toLocalDate(), start, end))
                || (task.getCompletedAt() != null && inRange(task.getCompletedAt().toLocalDate(), start, end));
    }

    private boolean inRange(LocalDate date, LocalDate start, LocalDate end) {
        return date != null && !date.isBefore(start) && !date.isAfter(end);
    }

    private boolean isCompleted(TaskAnalytics task) {
        return "COMPLETED".equalsIgnoreCase(task.getStatus());
    }

    private boolean isOverdue(TaskAnalytics task) {
        return task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now()) && !isCompleted(task);
    }

    private int estimatedMinutes(TaskAnalytics task) {
        return task.getEstimatedMinutes() == null ? 0 : task.getEstimatedMinutes();
    }

    private int actualMinutes(TaskAnalytics task) {
        return task.getActualMinutes() == null ? 0 : task.getActualMinutes();
    }

    private String category(TaskAnalytics task) {
        return task.getCategory() == null || task.getCategory().isBlank()
                ? "GENERAL"
                : task.getCategory().toUpperCase(Locale.ROOT);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

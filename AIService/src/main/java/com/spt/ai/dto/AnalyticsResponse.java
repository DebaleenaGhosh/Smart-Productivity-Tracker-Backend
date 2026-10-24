package com.spt.ai.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record AnalyticsResponse(
        String period,
        LocalDate startDate,
        LocalDate endDate,
        int totalTasks,
        int completedTasks,
        int overdueTasks,
        double completionRate,
        int totalEstimatedMinutes,
        int totalActualMinutes,
        List<TimeSeriesPoint> timeline,
        Map<String, Integer> categoryAllocation
) {
    public record TimeSeriesPoint(
            LocalDate date,
            int allocatedMinutes,
            int spentMinutes,
            int completedTasks
    ) { }
}

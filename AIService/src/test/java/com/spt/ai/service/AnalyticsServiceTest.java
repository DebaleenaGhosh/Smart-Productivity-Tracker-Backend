package com.spt.ai.service;

import com.spt.ai.dto.AnalyticsResponse;
import com.spt.ai.entity.TaskAnalytics;
import com.spt.ai.repository.TaskAnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private TaskAnalyticsRepository taskRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void buildsWeeklyAllocationAndCompletionMetrics() {
        TaskAnalytics completed = TaskAnalytics.builder()
                .taskId(10L)
                .userId(7L)
                .category("WORK")
                .status("COMPLETED")
                .estimatedMinutes(60)
                .actualMinutes(75)
                .dueDate(LocalDate.of(2026, 9, 29))
                .createdAt(LocalDateTime.of(2026, 9, 28, 10, 0))
                .completedAt(LocalDateTime.of(2026, 9, 29, 12, 0))
                .build();
        TaskAnalytics pending = TaskAnalytics.builder()
                .taskId(11L)
                .userId(7L)
                .category("LEARNING")
                .status("PENDING")
                .estimatedMinutes(30)
                .actualMinutes(0)
                .dueDate(LocalDate.of(2026, 10, 1))
                .createdAt(LocalDateTime.of(2026, 9, 30, 10, 0))
                .build();
        when(taskRepository.findByUserId(7L)).thenReturn(List.of(completed, pending));

        AnalyticsResponse response = analyticsService.getWeekly(7L, 2026, 40);

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 9, 28));
        assertThat(response.totalTasks()).isEqualTo(2);
        assertThat(response.completedTasks()).isEqualTo(1);
        assertThat(response.completionRate()).isEqualTo(50.0);
        assertThat(response.totalEstimatedMinutes()).isEqualTo(90);
        assertThat(response.totalActualMinutes()).isEqualTo(75);
        assertThat(response.categoryAllocation()).containsEntry("WORK", 60).containsEntry("LEARNING", 30);
        assertThat(response.timeline()).hasSize(7);
    }
}
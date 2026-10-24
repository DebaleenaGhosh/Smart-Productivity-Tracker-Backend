package com.spt.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {

    private EventMetadata metadata;
    private String eventType;
    private String eventId;
    private String taskEventType;  // e.g., "TASK_CREATED", "TASK_UPDATED", "TASK_DELETED"
    private Long taskId;
    private Long userId;
    private String title;
    private String category;
    private String priority;
    private String status;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    public enum taskEventType
    {
        TASK_CREATED,
        TASK_UPDATED,
        TASK_DELETED,
        ALL_TASKS_DELETED
    }
}
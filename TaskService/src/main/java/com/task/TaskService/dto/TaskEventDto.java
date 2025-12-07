package com.task.TaskService.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEventDto implements Serializable
{
    private String eventType;  // e.g., "TASK_CREATED", "TASK_UPDATED", "TASK_DELETED"
    private Long taskId;
    private Long userId;
    private Instant timestamp;
    private TaskDto task;  // Can be null for delete events
}
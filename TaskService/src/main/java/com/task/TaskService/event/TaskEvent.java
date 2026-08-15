package com.task.TaskService.event;

import java.io.Serializable;
import java.time.Instant;

import com.task.TaskService.dto.TaskDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEvent implements Serializable
{
    private String eventType;  // e.g., "TASK_CREATED", "TASK_UPDATED", "TASK_DELETED"
    private Long taskId;
    private Long userId;
    private Instant timestamp;
    private TaskDto task;  // Can be null for delete events
}
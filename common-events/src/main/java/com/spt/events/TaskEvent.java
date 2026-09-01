package com.spt.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public enum taskEventType
    {
        TASK_CREATED,
        TASK_DELETED,
        ALL_TASKS_DELETED
    }
}
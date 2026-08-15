package com.task.TaskService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent
{
    private String eventType;  // e.g., "CREATED", "UPDATED"
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private Instant timestamp;
}

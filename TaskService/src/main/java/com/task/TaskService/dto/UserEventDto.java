package com.task.TaskService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDto
{
    private String eventType;  // e.g., "CREATED", "UPDATED"
    private Long userId;
    private String userName;
    private String email;
    private String password;
    private String role;
    private Instant timestamp;
}

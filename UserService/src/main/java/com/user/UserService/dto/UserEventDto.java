package com.user.UserService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDto
{
    private String eventType;
    private Long userId;
    private Instant timestamp;
    private String username;
}

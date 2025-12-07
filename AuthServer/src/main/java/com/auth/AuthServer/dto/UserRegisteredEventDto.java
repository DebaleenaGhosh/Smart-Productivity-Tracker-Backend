package com.auth.AuthServer.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisteredEventDto implements Serializable
{
    private String eventType;  // e.g., "CREATED"
    private Long userId;
    private String userName;
    private String email;
    private String password;
    private String role;
    private Instant timestamp;
}

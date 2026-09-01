package com.spt.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    private EventMetadata metadata;
    private String eventType;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime timestamp;

    public enum UserEventType
    {
        USER_REGISTERED,
        USER_DELETED
    }

}
package com.user.UserService.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEventDto implements Serializable
{
    private Long taskId;
    private Long userId;
    private String eventType; // e.g., "CREATED"
}

package com.user.UserService.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceResponse
{
    private Long userId;
    private String username;
    private String email;
    private String role;
    private int taskCount;
}

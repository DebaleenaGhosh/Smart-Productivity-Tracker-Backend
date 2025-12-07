package com.user.UserService.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceResponse
{
    private String username;
    private String email;
    private String role;
    private HttpStatus httpStatus;
    private String httpMessage;
}

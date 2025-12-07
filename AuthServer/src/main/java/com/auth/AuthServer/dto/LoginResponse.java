package com.auth.AuthServer.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class LoginResponse
{
    private String token;
    private Long expiresIn;
    private HttpStatus httpStatus;
    private String httpMessage;
}

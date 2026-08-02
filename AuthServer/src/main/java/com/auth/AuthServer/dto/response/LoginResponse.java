package com.auth.AuthServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse
{
    private String token;
    private Long expiresIn;
}

package com.auth.AuthServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
public class RegisteredUserResponse
{
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private HttpStatus httpStatus;
    private String httpMessage;

    public RegisteredUserResponse setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public RegisteredUserResponse setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public RegisteredUserResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public RegisteredUserResponse setRole(String role) {
        this.role = role;
        return this;
    }

    public RegisteredUserResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public RegisteredUserResponse setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
        return this;
    }
}

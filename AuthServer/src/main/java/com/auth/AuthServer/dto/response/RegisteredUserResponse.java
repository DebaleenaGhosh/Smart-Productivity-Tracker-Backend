package com.auth.AuthServer.dto.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RegisteredUserResponse {
    private Long userId;
    private String userName;
    private String email;
    private String role;

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
}

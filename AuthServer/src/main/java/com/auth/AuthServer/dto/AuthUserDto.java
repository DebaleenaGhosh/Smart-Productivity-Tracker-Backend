package com.auth.AuthServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDto
{
    private Long userId;
    private String userName;
    private String password;
    private String email;
    private String role;

    public AuthUserDto setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public AuthUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public AuthUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public AuthUserDto setRole(String role) {
        this.role = role;
        return this;
    }
}

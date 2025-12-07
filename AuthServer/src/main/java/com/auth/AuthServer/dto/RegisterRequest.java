package com.auth.AuthServer.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest
{
    private String userName;
    private String password;
    private String email;
    private String role;
}

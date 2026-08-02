package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.request.LoginRequest;
import com.auth.AuthServer.dto.request.RegisterRequest;
import com.auth.AuthServer.dto.response.LoginResponse;
import com.auth.AuthServer.dto.response.RegisteredUserResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService
{
    LoginResponse authenticate(LoginRequest request);
    RegisteredUserResponse userRegistration(RegisterRequest request);
    void logout(String token);
}

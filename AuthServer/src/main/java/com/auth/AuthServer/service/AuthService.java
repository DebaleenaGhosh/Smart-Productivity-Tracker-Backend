package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService
{
    LoginResponse authenticate(LoginRequest request);
    RegisteredUserResponse userRegistration(RegisterRequest request);
    void logout(String token);
    void logoutSession(HttpServletRequest request);
}

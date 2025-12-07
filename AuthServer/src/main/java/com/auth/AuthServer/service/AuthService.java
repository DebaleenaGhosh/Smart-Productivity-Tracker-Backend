package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.*;

public interface AuthService
{
    LoginResponse authenticate(LoginRequest request);
    RegisteredUserResponse userRegistration(RegisterRequest request);
}

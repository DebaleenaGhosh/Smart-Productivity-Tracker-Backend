package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.LoginRequest;
import com.auth.AuthServer.dto.LoginResponse;
import com.auth.AuthServer.dto.RegisteredUserResponse;
import com.auth.AuthServer.entity.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService
{
    String generateToken(AuthUser user);
    boolean isTokenValid(String token, UserDetails userDetails);
    String extractUsername(String token);
    long getExpirationTime();
}

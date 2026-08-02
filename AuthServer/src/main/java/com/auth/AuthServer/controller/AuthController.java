package com.auth.AuthServer.controller;

import com.auth.AuthServer.dto.request.LoginRequest;
import com.auth.AuthServer.dto.request.RegisterRequest;
import com.auth.AuthServer.dto.response.LoginResponse;
import com.auth.AuthServer.dto.response.LogoutResponse;
import com.auth.AuthServer.dto.response.RegisteredUserResponse;
import com.auth.AuthServer.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController
{
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisteredUserResponse> userRegister(@Valid @RequestBody RegisterRequest registerRequest)
    {
        RegisteredUserResponse registeredUserResponse = authService.userRegistration(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredUserResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@Valid @RequestBody LoginRequest loginRequest)
    {
        LoginResponse loginResponse = authService.authenticate(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> userLogout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return ResponseEntity.ok(
                new LogoutResponse("Logged out successfully")
        );
    }
}

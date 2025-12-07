package com.auth.AuthServer.controller;

import com.auth.AuthServer.dto.*;
import com.auth.AuthServer.service.AuthService;
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
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisteredUserResponse> userRegister(@RequestBody RegisterRequest registerRequest)
    {
        RegisteredUserResponse registeredUserResponse = authService.userRegistration(registerRequest);
        return new ResponseEntity<>(registeredUserResponse, registeredUserResponse.getHttpStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest loginRequest)
    {
        LoginResponse loginResponse = authService.authenticate(loginRequest);
        return new ResponseEntity<>(loginResponse, loginResponse.getHttpStatus());
    }
}

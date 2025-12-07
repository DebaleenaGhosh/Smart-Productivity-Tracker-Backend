package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.*;
import com.auth.AuthServer.entity.AuthUser;
import com.auth.AuthServer.exception.AccessDeniedException;
import com.auth.AuthServer.exception.BadCredentialsException;
import com.auth.AuthServer.repository.AuthUserRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService
{
    @Autowired
    private final AuthUserRepository authUserRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    AuthEventPublisher authEventPublisher;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserEntityConverter converter;

    public AuthServiceImpl(AuthUserRepository authUserRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RegisteredUserResponse userRegistration(RegisterRequest input)
    {
        RegisteredUserResponse registeredUserResponse = new RegisteredUserResponse();
        try {
            if (authUserRepository.findByUserName(input.getUserName()).isPresent()
                    || authUserRepository.findByEmail(input.getEmail()).isPresent()) {
                throw new RuntimeException("Duplicate value exists");
            } else if (!StringUtils.isAlphanumeric(input.getUserName())) {
                throw new BadCredentialsException("Username is invalid!");
            }
            AuthUserDto authUserDto = new AuthUserDto()
                    .setUserName(input.getUserName())
                    .setEmail(input.getEmail())
                    .setRole(input.getRole())
                    .setPassword(passwordEncoder.encode(input.getPassword()));
            AuthUser authUser = authUserRepository.save(converter.convertDtoToEntity(authUserDto));

            // Publish event
            authEventPublisher.publishUserCreated(converter.convertEntityToDto(authUser));

            registeredUserResponse.setUserId(authUser.getUserId())
                                .setUserName(authUser.getUsername())
                                .setEmail(authUser.getEmail())
                                .setRole(authUser.getRole())
                                .setHttpStatus(HttpStatus.CREATED)
                                .setHttpMessage("User created successfully");
        }
        catch(BadCredentialsException | RuntimeException exception)
        {
            registeredUserResponse.setHttpStatus(HttpStatus.BAD_REQUEST)
                                .setHttpMessage(exception.getMessage());
            return registeredUserResponse;
        }
        return registeredUserResponse;
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        try
        {
            // Perform authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );
            // Extract authenticated user details
            AuthUser authenticatedAuthUser = authUserRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new AccessDeniedException("Not authorized"));

            // Generate JWT for this authenticated user
            String token = jwtService.generateToken(authenticatedAuthUser);

            // Prepare response
            loginResponse.setToken(token);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setHttpStatus(HttpStatus.ACCEPTED);
            loginResponse.setHttpMessage("Logged in successfully");
        }
        catch(AccessDeniedException accessDeniedException)
        {
            loginResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
            loginResponse.setHttpMessage(accessDeniedException.getMessage());
        }
        return loginResponse;
    }
}


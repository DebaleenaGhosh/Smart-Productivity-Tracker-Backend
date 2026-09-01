package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.*;
import com.auth.AuthServer.dto.request.LoginRequest;
import com.auth.AuthServer.dto.request.RegisterRequest;
import com.auth.AuthServer.dto.response.LoginResponse;
import com.auth.AuthServer.dto.response.RegisteredUserResponse;
import com.auth.AuthServer.entity.AuthUser;
import com.auth.AuthServer.entity.BlackListedToken;
import com.auth.AuthServer.event.UserEventPublisher;
import com.auth.AuthServer.exception.AccessDeniedException;
import com.auth.AuthServer.exception.BadCredentialsException;
import com.auth.AuthServer.exception.DuplicateResourceException;
import com.auth.AuthServer.mapper.UserEntityMapper;
import com.auth.AuthServer.repository.AuthUserRepository;
import com.auth.AuthServer.repository.BlackListedTokenRepository;
import com.spt.events.UserEvent;
import com.spt.events.EventMetadata;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService
{
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserEntityMapper mapper;
    private final BlackListedTokenRepository blacklistRepo;
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final UserEventPublisher userEventPublisher;

    public AuthServiceImpl(AuthUserRepository authUserRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           BlackListedTokenRepository blacklistRepo,
                           JwtService jwtService,
                           UserEntityMapper mapper,
                           BlackListedTokenRepository blacklistRepo1,
                           UserEventPublisher userEventPublisher) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.mapper = mapper;
        this.blacklistRepo = blacklistRepo1;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public RegisteredUserResponse userRegistration(RegisterRequest input)
    {
        if(authUserRepository.findByUserName(input.getUserName()).isPresent()){
            throw new DuplicateResourceException("Username already exists");
        }

        if(authUserRepository.findByEmail(input.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exists");
        }

        if(!StringUtils.isAlphanumeric(input.getUserName())){
            throw new BadCredentialsException("Username can contain only numbers and letters");
        }

        RegisteredUserResponse registeredUserResponse = new RegisteredUserResponse();

        AuthUserDto authUserDto = new AuthUserDto()
                .setUserName(input.getUserName())
                .setEmail(input.getEmail())
                .setRole(input.getRole())
                .setPassword(passwordEncoder.encode(input.getPassword()));
        AuthUser authUser = authUserRepository.save(mapper.convertDtoToEntity(authUserDto));

        // Publish event
        UserEvent event = UserEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(UUID.randomUUID().toString())
                                .eventType("USER_REGISTERED")
                                .timestamp(Instant.now())
                                .source("AUTH-SERVICE")
                                .build()
                )
                .userId(authUser.getUserId())
                .username(authUser.getUsername())
                .email(authUser.getEmail())
                .role(authUser.getRole())
                .build();

        userEventPublisher.publishUserRegistered(event);

        log.info(
                "User registered successfully. userId = {}", authUser.getUserId()
        );

        registeredUserResponse.setUserId(authUser.getUserId())
                .setUserName(authUser.getUsername())
                .setEmail(authUser.getEmail())
                .setRole(authUser.getRole());
        return registeredUserResponse;
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest)
    {
        try {
            // Perform authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }
        catch (org.springframework.security.authentication.BadCredentialsException exception) {
            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }
        // Extract authenticated user details
        AuthUser authenticatedAuthUser = authUserRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new AccessDeniedException("User account could not be found"));

        // Generate JWT for this authenticated user
        String token = jwtService.generateToken(authenticatedAuthUser);

        log.info(
                "User authenticated successfully. userId={}",
                authenticatedAuthUser.getUserId()
        );

        // Prepare response
        return new LoginResponse(
                token,
                jwtService.getExpirationTime()
        );
    }

    @Override
    public void logout(String token)
    {
        if (token == null || token.isBlank()) return;

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date exp = claims.getExpiration();
            Instant expiry = (exp != null)
                    ? exp.toInstant()
                    : Instant.now().plusSeconds(3600);

            BlackListedToken bt = new BlackListedToken();
            bt.setToken(token);
            bt.setExpiry(expiry);
            blacklistRepo.save(bt);

        } catch (Exception e) {
            // even if token is bad, still block it briefly
            BlackListedToken bt = new BlackListedToken();
            bt.setToken(token);
            bt.setExpiry(Instant.now().plusSeconds(300));
            blacklistRepo.save(bt);
        }
    }

// SessionCreationPolicy.STATELESS is defined in security configuration hence logout session not required

//    @Override
//    public void logoutSession(HttpServletRequest request) {
//        try {
//            var session = request.getSession(false);
//            if (session != null) session.invalidate();
//            // also clear security context if using Spring Security
//            org.springframework.security.core.context.SecurityContextHolder.clearContext();
//        } catch (Exception ignored) {}
//    }
//
//    // periodic cleanup of expired blacklisted tokens
//    @Scheduled(fixedDelayString = "PT1H")
//    public void cleanupExpired() {
//        blacklistRepo.deleteByExpiryBefore(Instant.now());
//    }
}


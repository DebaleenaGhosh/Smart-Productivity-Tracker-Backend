package com.auth.AuthServer.service;

import com.auth.AuthServer.dto.*;
import com.auth.AuthServer.entity.AuthUser;
import com.auth.AuthServer.entity.BlackListedToken;
import com.auth.AuthServer.exception.AccessDeniedException;
import com.auth.AuthServer.exception.BadCredentialsException;
import com.auth.AuthServer.repository.AuthUserRepository;
import com.auth.AuthServer.repository.BlackListedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService
{
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Autowired
    AuthEventPublisher authEventPublisher;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserEntityConverter converter;
    private final BlackListedTokenRepository blacklistRepo;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthServiceImpl(AuthUserRepository authUserRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           BlackListedTokenRepository blacklistRepo, BlackListedTokenRepository blacklistRepo1) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.blacklistRepo = blacklistRepo1;
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

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) return;
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Date exp = claims.getExpiration();
            Instant expiry = (exp != null) ? exp.toInstant() : Instant.now().plusSeconds(3600);

            // clear token on the user record (if present)
            authUserRepository.findByToken(token).ifPresent(user -> {
                user.setToken(null);
                authUserRepository.save(user);
            });

            BlackListedToken bt = new BlackListedToken();
            bt.setToken(token);
            bt.setExpiry(expiry);
            blacklistRepo.save(bt);
        } catch (Exception e) {
            // Token invalid -> still store short-lived block to be safe
            authUserRepository.findByToken(token).ifPresent(user -> {
                user.setToken(null);
                authUserRepository.save(user);
            });
            BlackListedToken bt = new BlackListedToken();
            bt.setToken(token);
            bt.setExpiry(Instant.now().plusSeconds(300));
            blacklistRepo.save(bt);
        }
    }

    @Override
    public void logoutSession(HttpServletRequest request) {
        try {
            var session = request.getSession(false);
            if (session != null) session.invalidate();
            // also clear security context if using Spring Security
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        } catch (Exception ignored) {}
    }

    // periodic cleanup of expired blacklisted tokens
    @Scheduled(fixedDelayString = "PT1H")
    public void cleanupExpired() {
        blacklistRepo.deleteByExpiryBefore(Instant.now());
    }
}


package com.auth.AuthServer.service;

import com.auth.AuthServer.config.RabbitCommonConfig;
import com.auth.AuthServer.dto.AuthUserDto;
import com.auth.AuthServer.dto.LoginRequest;
import com.auth.AuthServer.dto.UserRegisteredEventDto;
import com.auth.AuthServer.entity.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
public class AuthEventPublisher
{
    private final RabbitTemplate rabbitTemplate;

    // Publish user created event
    public void publishUserCreated(AuthUserDto authUserDto)
    {
        UserRegisteredEventDto userRegisteredEventDto = new UserRegisteredEventDto(
                "USER_CREATED",
                authUserDto.getUserId(),
                authUserDto.getUserName(),
                authUserDto.getEmail(),
                authUserDto.getPassword(),
                authUserDto.getRole(),
                Instant.now());
        rabbitTemplate.convertAndSend(
                RabbitCommonConfig.USER_EVENTS_EXCHANGE,
                "user.registered",     // MUST MATCH binding key
                userRegisteredEventDto);
        log.info("Published user.created event for userId={}",authUserDto.getUserId());
    }
}

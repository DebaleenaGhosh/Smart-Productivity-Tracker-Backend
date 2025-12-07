package com.user.UserService.service;

import com.user.UserService.config.RabbitCommonConfig;
import com.user.UserService.dto.UserDto;
import com.user.UserService.dto.UserEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher
{
    private final RabbitTemplate rabbitTemplate;

    // Publish user deleted event
    public void publishUserDeleted(Long userId)
    {
        UserEventDto userEventDto = new UserEventDto("USER_DELETED",userId,Instant.now(),null);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.USER_EVENTS_EXCHANGE,"user.deleted",userEventDto);
        log.info("Published user.deleted event for userId={}", userId);
    }
    // Publish user updated event
    public void publishUserUpdated(UserDto userDto)
    {
        UserEventDto userEventDto = new UserEventDto("USER_UPDATED",userDto.getId(),Instant.now(),userDto.getUsername());
        rabbitTemplate.convertAndSend(RabbitCommonConfig.USER_EVENTS_EXCHANGE,"user.updated",userEventDto);
        log.info("Published task.updated event for userId={}",userDto.getId());
    }

//    // Publish user created event
//    public void publishUserCreated(UserDto userDto)
//    {
//        UserEventDto userEventDto = new UserEventDto("USER_CREATED",userDto.getId(),Instant.now(),userDto.getUsername());
//        rabbitTemplate.convertAndSend(RabbitCommonConfig.USER_EVENTS_EXCHANGE,"user.created",userEventDto);
//        log.info("Published user.created event for userId={}",userDto.getId());
//    }
}

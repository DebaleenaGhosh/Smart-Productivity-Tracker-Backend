package com.user.UserService.service;

import com.user.UserService.config.RabbitCommonConfig;
import com.user.UserService.dto.TaskEventDto;
import com.user.UserService.dto.UserDto;
import com.user.UserService.dto.UserEntityConverter;
import com.user.UserService.dto.UserRegisteredEventDto;
import com.user.UserService.entity.UserProfile;
import com.user.UserService.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventListener
{
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserEntityConverter converter;

    @RabbitListener(queues = RabbitCommonConfig.USER_REGISTERED_QUEUE )
    public void handleUserRegistered(UserRegisteredEventDto event)
    {
        log.info("User registration event received: {} for user {} (ID: {})", event.getEventType(), event.getUserName(), event.getUserId());
        UserDto userDto = new UserDto();
        userDto.setUsername(event.getUserName());
        userDto.setPassword(event.getPassword());
        userDto.setEmail(event.getEmail());
        userDto.setRole(event.getRole());
        userDto.setTaskCount(0L);
        userRepository.save(converter.convertDtoToEntity(userDto));
    }

    @RabbitListener(queues = RabbitCommonConfig.USER_TASK_EVENTS_QUEUE)
    public void onTaskEvent(TaskEventDto dto)
    {
        log.info("UserProfile Service received task event: {} for userID {}", dto.getEventType(), dto.getUserId());
        // Example: increment user's task count, send notification, etc.
        userService.incrementTaskCount(dto.getUserId());
    }
}

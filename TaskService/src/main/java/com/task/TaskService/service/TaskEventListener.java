package com.task.TaskService.service;

import com.task.TaskService.config.RabbitCommonConfig;
import com.task.TaskService.dto.TaskServiceResponse;
import com.task.TaskService.dto.UserEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskEventListener
{
    @Autowired
    private TaskService taskService;

    @RabbitListener(queues = RabbitCommonConfig.TASK_USER_EVENTS_QUEUE )
    public void onUserEvent(UserEventDto dto)
    {
        log.info("Task Service received user event: {} for user ID: {})", dto.getEventType(), dto.getUserId());

        // User Created/Registered event
        if (dto.getEventType().contains("CREATED"))
        {
            // When a user is created, create a default task or associate existing tasks
            TaskServiceResponse defaultTask = taskService.createDefaultTaskForUser(dto.getUserId());
            log.info("Created default task for new user: {} : {}", dto.getUserName(), defaultTask.getHttpMessage());
        }
        else if (dto.getEventType().contains("UPDATED"))
        {
            // When a user is updated, re-sync tasks or send notifications
            taskService.syncTasksForUser(dto.getUserId());
            log.info("Synced tasks for updated user ID: {}", dto.getUserId());
        }
        else if(dto.getEventType().contains("DELETED"))
        {
            // When a user is deleted, delete all the corresponding tasks
            taskService.deleteAllTasksByUserId(dto.getUserId());
            log.info("Tasks deleted for user with ID: {}", dto.getUserId());
        }
    }
}

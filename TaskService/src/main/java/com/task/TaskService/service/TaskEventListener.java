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
public class TaskEventListener {

    @Autowired
    private TaskService taskService;    // Assuming TaskService exists for task operations

    @RabbitListener(queues = RabbitCommonConfig.TASK_USER_EVENTS_QUEUE )
    public void onUserRegistrationEvent(UserEventDto dto) {
        log.info("Task Service received user event: {} for user {} (ID: {})", dto.getEventType(), dto.getUserName(), dto.getUserId());

        // Example actions based on event type:
        if (dto.getEventType().contains("CREATED")) {
            // When a user is created, create a default task or associate existing tasks
            TaskServiceResponse defaultTask = taskService.createDefaultTaskForUser(dto.getUserId());
            log.info("Created default task for new user: {} : {} , {}", dto.getUserName(), defaultTask.getStatus(), defaultTask.getHttpMessage());
        }
        else if (dto.getEventType().contains("UPDATED")) {
            // When a user is updated, perhaps re-sync tasks or send notifications
            taskService.syncTasksForUser(dto.getUserId());
            log.info("Synced tasks for updated user: {}", dto.getUserName());
        }
        // Add more event types as needed (e.g., "deleted")
    }
}

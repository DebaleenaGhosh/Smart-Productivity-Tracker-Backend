package com.task.TaskService.service;

import com.task.TaskService.config.RabbitCommonConfig;
import com.task.TaskService.dto.TaskDto;
import com.task.TaskService.event.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher
{
    private final RabbitTemplate rabbitTemplate;

    // Publish task created event
    public void publishTaskCreated(TaskDto taskDto)
    {
        TaskEvent taskEvent = new TaskEvent("TASK_CREATED",taskDto.getTaskId(), taskDto.getUserId(),Instant.now(),taskDto);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"task.created", taskEvent);
        log.info("Published task.created event for taskId={}",taskDto.getTaskId());
    }
    // Publish task deleted event
    public void publishTaskDeleted(Long taskId, Long userId)
    {
        TaskEvent taskEvent = new TaskEvent("TASK_DELETED",taskId,userId,Instant.now(),null);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"task.deleted", taskEvent);
        log.info("Published task.deleted event for taskId={}", taskId);
    }
    // Publish task updated event
    public void publishTaskUpdated(TaskDto taskDto)
    {
        TaskEvent taskEvent = new TaskEvent("TASK_UPDATED",taskDto.getTaskId(),taskDto.getUserId(),Instant.now(),taskDto);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"task.updated", taskEvent);
        log.info("Published task.updated event for taskId={}",taskDto.getTaskId());
    }
    // Publish all tasks deleted event
    public void publishAllTasksDeleted(Long userId)
    {
        TaskEvent taskEvent = new TaskEvent("ALL_TASKS_DELETED",null, userId,Instant.now(),null);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"tasks.deleted", taskEvent);
        log.info("Published task.deleted event for userId = {}", userId);
    }
}

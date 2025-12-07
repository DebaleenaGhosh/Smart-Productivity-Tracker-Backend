package com.task.TaskService.service;

import com.task.TaskService.config.RabbitCommonConfig;
import com.task.TaskService.dto.TaskDto;
import com.task.TaskService.dto.TaskEventDto;
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
        TaskEventDto taskEventDto = new TaskEventDto("TASK_CREATED",taskDto.getTaskId(), taskDto.getUserId(),Instant.now(),taskDto);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"task.created",taskEventDto);
        log.info("Published task.created event for taskId={}",taskDto.getTaskId());
    }
    // Publish task deleted event
    public void publishTaskDeleted(Long taskId, Long userId)
    {
        TaskEventDto taskEventDto = new TaskEventDto("TASK_DELETED",taskId,userId,Instant.now(),null);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"task.deleted",taskEventDto);
        log.info("Published task.deleted event for taskId={}", taskId);
    }
    // Publish task updated event
    public void publishTaskUpdated(TaskDto taskDto)
    {
        TaskEventDto taskEventDto = new TaskEventDto("TASK_UPDATED",taskDto.getTaskId(),taskDto.getUserId(),Instant.now(),taskDto);
        rabbitTemplate.convertAndSend(RabbitCommonConfig.TASK_EVENTS_EXCHANGE,"task.updated",taskEventDto);
        log.info("Published task.updated event for taskId={}",taskDto.getTaskId());
    }

}

package com.task.TaskService.event;

import com.task.TaskService.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.spt.events.TaskEvent;
import com.spt.events.EventMetadata;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher
{
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTaskCreated( TaskDto task )
    {
        TaskEvent event = TaskEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(UUID.randomUUID().toString() )
                                .eventType("TASK_CREATED")
                                .timestamp(Instant.now())
                                .source("TASK-SERVICE")
                                .build()
                )
                .taskId(task.getTaskId())
                .userId(task.getUserId())
                .build();

        kafkaTemplate.send(
                "task.events",
                task.getUserId().toString(),
                event
        );

        log.info(
                "Published Task_Created. taskId = {}, userId = {}", event.getTaskId(), event.getUserId()
        );
    }

    public void publishTaskDeleted( Long taskId, Long userId )
    {
        TaskEvent event = TaskEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(UUID.randomUUID().toString() )
                                .eventType("TASK_DELETED")
                                .timestamp(Instant.now())
                                .source("TASK-SERVICE")
                                .build()
                )
                .taskId(taskId)
                .userId(userId)
                .build();

        kafkaTemplate.send(
                "task.events",
                userId.toString(),
                event
        );

        log.info(
                "Published Task_Deleted. taskId = {}, userId = {}", event.getTaskId(), event.getUserId()
        );
    }

    public void publishAllTasksDeleted( Long userId )
    {
        TaskEvent event = TaskEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(UUID.randomUUID().toString() )
                                .eventType("ALL_TASKS_DELETED")
                                .timestamp(Instant.now())
                                .source("TASK-SERVICE")
                                .build()
                )
                .userId(userId)
                .build();

        kafkaTemplate.send(
                "task.events",
                userId.toString(),
                event
        );

        log.info(
                "Published All_Tasks_Deleted. userId = {}", event.getUserId()
        );
    }
}

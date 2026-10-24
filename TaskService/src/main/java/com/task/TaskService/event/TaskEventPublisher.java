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

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher
{
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTaskCreated( TaskDto task )
    {
        publishTaskSnapshot(task, "TASK_CREATED");
    }

    public void publishTaskUpdated(TaskDto task)
    {
        publishTaskSnapshot(task, "TASK_UPDATED");
    }

    public void publishTaskDeleted( Long taskId, Long userId )
    {
        String eventId = UUID.randomUUID().toString();
        TaskEvent event = TaskEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(eventId)
                                .eventType("TASK_DELETED")
                                .timestamp(Instant.now())
                                .source("TASK-SERVICE")
                                .build()
                )
                .eventType("TASK_DELETED")
                .eventId(eventId)
                .taskEventType("TASK_DELETED")
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
                String eventId = UUID.randomUUID().toString();
        TaskEvent event = TaskEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(eventId)
                                .eventType("ALL_TASKS_DELETED")
                                .timestamp(Instant.now())
                                .source("TASK-SERVICE")
                                .build()
                )
                .eventType("ALL_TASKS_DELETED")
                .eventId(eventId)
                .taskEventType("ALL_TASKS_DELETED")
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

    private void publishTaskSnapshot(TaskDto task, String eventType)
    {
        String eventId = UUID.randomUUID().toString();
        TaskEvent event = TaskEvent.builder()
                .metadata(EventMetadata.builder()
                        .eventId(eventId)
                        .eventType(eventType)
                        .timestamp(Instant.now())
                        .source("TASK-SERVICE")
                        .build())
                .eventType(eventType)
                .eventId(eventId)
                .taskEventType(eventType)
                .taskId(task.getTaskId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .category(task.getCategory())
                .priority(task.getPriority())
                .status(task.getStatus())
                .estimatedMinutes(task.getEstimatedMinutes())
                .actualMinutes(task.getActualMinutes())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .updatedAt(task.getUpdatedAt())
                .build();

        kafkaTemplate.send("task.events", task.getUserId().toString(), event);
        log.info("Published {}. taskId={}, userId={}", eventType, task.getTaskId(), task.getUserId());
    }
}

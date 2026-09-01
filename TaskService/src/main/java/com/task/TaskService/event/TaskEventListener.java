package com.task.TaskService.event;

import com.task.TaskService.entity.ProcessedEvent;
import com.task.TaskService.repository.ProcessedEventRepository;
import com.task.TaskService.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.spt.events.UserEvent;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventListener
{
    private final TaskService taskService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "user.events",
            groupId = "task-service"
    )

    @Transactional
    public void handleUserEvent( UserEvent event )
    {
        log.info("Received user event: {}", event.getMetadata().getEventType());

        if( "USER_REGISTERED".equals(event.getMetadata().getEventType()))
        {
            // Prevent duplicate processing
            if( processedEventRepository.existsById(event.getMetadata().getEventId()) ){
                log.info("User Registration Event already processed: {}", event.getMetadata().getEventId());
                return;
            }

            taskService.createDefaultTaskForUser( event.getUserId() );

            // Mark event as processed
            ProcessedEvent processedEvent = new ProcessedEvent(event.getMetadata().getEventId(), LocalDateTime.now());
            processedEventRepository.save(processedEvent);

            log.info("Successfully processed USER_REGISTERED event: {}", event.getMetadata().getEventId());
        }

        if( "USER_DELETED".equals(event.getMetadata().getEventType()))
        {
            // Prevent duplicate processing
            if( processedEventRepository.existsById(event.getMetadata().getEventId()) ){
                log.info("User Deleted Event already processed: {}", event.getMetadata().getEventId());
                return;
            }

            taskService.deleteAllTasksByUserId(
                    event.getUserId()
            );

            // Mark event as processed
            ProcessedEvent processedEvent = new ProcessedEvent(event.getMetadata().getEventId(), LocalDateTime.now());
            processedEventRepository.save(processedEvent);

            log.info("Successfully processed USER_DELETED event: {}", event.getMetadata().getEventId());
        }

    }
}

package com.user.UserService.event;

import com.user.UserService.entity.ProcessedEvent;
import com.user.UserService.repository.ProcessedEventRepository;
import com.user.UserService.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.spt.events.TaskEvent;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventListener
{
    private final UserService userService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "task.events",
            groupId = "user-service"
    )

    @Transactional
    public void consumeTaskEvent( TaskEvent taskEvent )
    {
        log.info(
                "Received task event. eventType = {}, taskId = {}",
                taskEvent.getMetadata().getEventType(),
                taskEvent.getTaskId()
        );
        if( "TASK_CREATED".equals(taskEvent.getMetadata().getEventType()))
        {
            if (taskEvent.getUserId() == null) {
                log.error(
                        "Invalid TaskEvent received. userId is null. eventId={}, taskId={}",
                        taskEvent.getMetadata().getEventId(),
                        taskEvent.getTaskId()
                );

                return;
            }

            if (taskEvent.getTaskId() == null) {
                log.error(
                        "Invalid TaskEvent received. taskId is null. eventId={}, userId={}",
                        taskEvent.getMetadata().getEventId(),
                        taskEvent.getUserId()
                );
                return;
            }

            // Prevent duplicate processing
            if( processedEventRepository.existsById(taskEvent.getMetadata().getEventId()) ){
                log.info("Task creation Event already processed: {}", taskEvent.getEventId());
                return;
            }
            userService.taskCountUpdate( taskEvent.getUserId(), "Increment" );

            // Mark event as processed
            ProcessedEvent processedEvent = new ProcessedEvent(taskEvent.getMetadata().getEventId(), LocalDateTime.now());
            processedEventRepository.save(processedEvent);

            log.info("Successfully processed TASK_CREATED event: {}", taskEvent.getMetadata().getEventId());
        }

        if( "TASK_DELETED".equals(taskEvent.getMetadata().getEventType()))
        {
            // Prevent duplicate processing
            if( processedEventRepository.existsById(taskEvent.getEventId()) ){
                log.info("Task Deletion Event already processed: {}", taskEvent.getEventId());
                return;
            }
            userService.taskCountUpdate( taskEvent.getUserId(), "Decrement" );

            // Mark event as processed
            ProcessedEvent processedEvent = new ProcessedEvent(taskEvent.getMetadata().getEventId(), LocalDateTime.now());
            processedEventRepository.save(processedEvent);

            log.info("Successfully processed TASK_DELETED event: {}", taskEvent.getMetadata().getEventId());
        }

        if( "ALL_TASKS_DELETED".equals(taskEvent.getMetadata().getEventType()))
        {
            // Prevent duplicate processing
            if( processedEventRepository.existsById(taskEvent.getMetadata().getEventId()) ){
                log.info("All Tasks Deletion Event already processed: {}", taskEvent.getMetadata().getEventId());
                return;
            }
            userService.taskCountUpdate( taskEvent.getUserId(), "Reset" );

            // Mark event as processed
            ProcessedEvent processedEvent = new ProcessedEvent(taskEvent.getMetadata().getEventId(), LocalDateTime.now());
            processedEventRepository.save(processedEvent);

            log.info("Successfully processed ALL_TASKS_DELETED event: {}", taskEvent.getMetadata().getEventId());
        }
    }
}

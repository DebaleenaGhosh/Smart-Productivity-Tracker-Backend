package com.user.UserService.event;

import com.user.UserService.entity.ProcessedEvent;
import com.user.UserService.repository.ProcessedEventRepository;
import com.user.UserService.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.spt.events.UserEvent;
import com.spt.events.TaskEvent;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener
{
    private final UserService userService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "user.events",
            groupId = "user-service"
    )

    @Transactional
    public void consumeUserRegisteredEvent( UserEvent event )
    {
        log.info(
                "Received user event. eventType = {}, eventId={}, userId = {}",
                event.getMetadata().getEventType(),
                event.getMetadata().getEventId(),
                event.getUserId()
        );
        if( "USER_REGISTERED".equals(event.getMetadata().getEventType()))
        {
            // Prevent duplicate processing
            if( processedEventRepository.existsById(event.getMetadata().getEventId()) ){
                log.info("User Registration Event already processed: {}", event.getMetadata().getEventId());
                return;
            }
            userService.createUserProfile(event);

            // Mark event as processed
            ProcessedEvent processedEvent = new ProcessedEvent(event.getMetadata().getEventId(), LocalDateTime.now());
            processedEventRepository.save(processedEvent);

            log.info("Successfully processed USER_REGISTERED event: {}", event.getMetadata().getEventId());
        }
    }
}

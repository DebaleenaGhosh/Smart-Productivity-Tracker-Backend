package com.auth.AuthServer.event;

import com.auth.AuthServer.entity.ProcessedEvent;
import com.auth.AuthServer.repository.AuthUserRepository;
import com.auth.AuthServer.repository.ProcessedEventRepository;
import com.auth.AuthServer.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import com.spt.events.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class UserEventListener {

    private final ProcessedEventRepository processedEventRepository;
    private final AuthUserRepository authUserRepository;

    public UserEventListener(
            ProcessedEventRepository processedEventRepository,
            AuthService authService,
            AuthUserRepository authUserRepository) {

        this.processedEventRepository = processedEventRepository;
        this.authUserRepository = authUserRepository;
    }

    @KafkaListener(
            topics = "user.events",
            groupId = "auth-service"
    )
    @Transactional
    public void handleUserEvent(UserEvent event) {

        log.info(
                "Authentication Service received event: {}", event.getMetadata().getEventType()
        );

        if ("USER_DELETED".equals(event.getMetadata().getEventType()))
        {
            handleUserDeleted(event);
            return;
        }

        log.info(
                "Authentication Service ignoring event: {}",event.getMetadata().getEventType()
        );
    }

    private void handleUserDeleted(UserEvent event) {

        /*
         * Prevent duplicate processing.
         */
        if (processedEventRepository.existsById(event.getMetadata().getEventId())) {

            log.info(
                    "USER_DELETED event already processed: {}",event.getMetadata().getEventId()
            );

            return;
        }

        /*
         * Delete the authentication record associated
         * with the deleted user.
         */
        authUserRepository.deleteById(event.getUserId());

        /*
         * Mark the Kafka event as processed.
         */
        processedEventRepository.save(
                new ProcessedEvent(
                        event.getMetadata().getEventId(),
                        java.time.LocalDateTime.now()
                )
        );

        log.info(
                "USER_DELETED successfully processed for user: {}",event.getUserId()
        );
    }
}
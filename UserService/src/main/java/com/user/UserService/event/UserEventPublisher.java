package com.user.UserService.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.spt.events.UserEvent;
import com.spt.events.EventMetadata;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher
{
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserDeleted( Long userId )
    {
        UserEvent event = UserEvent.builder()
                .metadata(
                        EventMetadata.builder()
                                .eventId(UUID.randomUUID().toString() )
                                .eventType("USER_DELETED")
                                .timestamp(Instant.now())
                                .source("USER-SERVICE")
                                .build()
                )
                .userId( userId )
                .build();

        kafkaTemplate.send(
                "user.events",
                userId.toString(),
                event
        );

        log.info(
                "Published User_Deleted. userId = {}", event.getUserId()
        );
    }
}

package com.auth.AuthServer.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.spt.events.UserEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher
{
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered( UserEvent event )
    {
        kafkaTemplate.send(
                "user.events",
                event.getUserId().toString(),
                event
        );

        log.info(
                "Published USER_REGISTERED event. userId = {}",
                event.getUserId()
        );
    }
}

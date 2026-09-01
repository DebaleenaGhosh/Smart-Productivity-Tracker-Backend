package com.spt.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadata {

    private String eventId;

    private String eventType;

    private Instant timestamp;

    private String source;
}

package com.spt.ai.dto;

import java.time.LocalDateTime;

public record SuggestionResponse(
        Long suggestionId,
        String type,
        String title,
        String message,
        LocalDateTime createdAt,
        boolean read,
        String source
) { }

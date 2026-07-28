package com.example.LoggingService.dto;

import java.time.Instant;

public record LogMessage(
        String message,
        String messageType,
        Instant dateTime
) {
}
package com.example.Transaction_Service.dto;

import java.time.Instant;

public record LogMessage(
        String message,
        String messageType,
        Instant dateTime
) {
}

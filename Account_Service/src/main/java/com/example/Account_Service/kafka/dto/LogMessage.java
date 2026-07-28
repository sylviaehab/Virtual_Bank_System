package com.example.Account_Service.kafka.dto;

import java.time.Instant;

public record LogMessage(
        String message,

        String messageType,

        Instant dateTime) {
}

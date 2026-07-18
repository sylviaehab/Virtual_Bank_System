package com.example.User_Service.dto;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
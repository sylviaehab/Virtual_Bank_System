package com.User_Service.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String username,
        String message
) {
}
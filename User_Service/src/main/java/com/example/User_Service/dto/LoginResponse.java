package com.example.User_Service.dto;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String username
) {
}
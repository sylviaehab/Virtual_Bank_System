package com.example.BFF_Service.dto;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        String email,
        String firstName,
        String lastName) {
}


package com.example.User_Service.dto;

import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
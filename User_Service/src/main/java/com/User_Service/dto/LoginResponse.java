package com.User_Service.dto;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String username
) {
}
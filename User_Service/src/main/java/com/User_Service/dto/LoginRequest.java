package com.User_Service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login credentials")
public record LoginRequest(
        @Schema(
                description = "Registered username",
                example = "abcxyz"
        )
        @NotBlank(message = "Username is required")
        String username,
        @Schema(
                description = "User's raw password",
                example = "Password123"
        )
        @NotBlank(message = "Password is required")
        String password

) {
}
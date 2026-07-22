package com.User_Service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Schema(description = "Information required to register a user")
public record RegisterRequest(
        @Schema(
                description = "Unique username",
                example = "maria"
        )
        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters"
        )
        String username,

        @Schema(
                description = "Raw password that will be hashed before storage",
                example = "Password123"
        )

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 100,
                message = "Password must contain between 8 and 100 characters"
        )
        String password,

        @Schema(
                description = "Unique valid email address",
                example = "abc@example.com"
        )

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(
                description = "User's first name",
                example = "abc"
        )

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @Schema(
                description = "User's last name",
                example = "xyz"
        )

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName
) {
}
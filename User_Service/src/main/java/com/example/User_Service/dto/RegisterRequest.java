package com.example.User_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 100,
                message = "Password must contain between 8 and 100 characters"
        )
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName
) {
}
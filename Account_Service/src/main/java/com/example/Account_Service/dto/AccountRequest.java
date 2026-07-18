package com.example.Account_Service.dto;

import com.example.Account_Service.Enum.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequest(
        @NotNull(message = "User_Id is required")
        UUID userId,
        @NotNull(message = "Account_Type is required")
        AccountType accountType,
        @NotNull(message = "Initial_Balance is required")
        @DecimalMin(value = "1000", message = "Balance must be greater than 1000")
        BigDecimal initialBalance
) {
}

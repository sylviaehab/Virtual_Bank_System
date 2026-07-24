package com.example.Account_Service.dto;

import com.example.Account_Service.enums.AccountType;
import com.example.Account_Service.validation.ValidEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequest(
        @NotNull(message = "User_Id is required")
        UUID userId,
        @ValidEnum(
                enumClass = AccountType.class,
                message = "Valid Account Types :SAVINGS, CHECKING, SYSTEM"
        )
        @NotBlank(message = "AccountType  is required")
        String accountType,
        @NotNull(message = "Initial_Balance is required")
        @DecimalMin(value = "1000", message = "Balance must be greater than 1000")
        BigDecimal initialBalance
) {
}

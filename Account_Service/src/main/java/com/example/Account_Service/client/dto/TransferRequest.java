package com.example.Account_Service.client.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "FromAccountId is required")
        UUID fromAccountId,
        @NotNull(message = "ToAccountId is required")
        UUID toAccountId,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount

) {
}

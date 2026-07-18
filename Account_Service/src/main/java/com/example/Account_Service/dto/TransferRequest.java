package com.example.Account_Service.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "fromAccountId is required")
        UUID fromAccountId,
        @NotNull(message = "toAccountId is required")
        UUID toAccountId,
        @NotNull(message = "amount is required")
        BigDecimal amount

) {
}

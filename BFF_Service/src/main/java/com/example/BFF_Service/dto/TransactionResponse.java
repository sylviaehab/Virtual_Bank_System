package com.example.BFF_Service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        UUID toAccountId,
        BigDecimal amount,
        String description,
        Instant timestamp) {
}

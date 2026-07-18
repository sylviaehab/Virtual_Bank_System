package com.example.Account_Service.dto;

import com.example.Account_Service.Enum.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record RetrieveResponse(
        UUID accountId,
        UUID accountNumber,
        AccountType accountType,
        BigDecimal balance,
        String status

) {
}

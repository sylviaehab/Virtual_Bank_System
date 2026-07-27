package com.example.BFF_Service.dto;

import com.example.BFF_Service.enums.AccountType;
import com.example.BFF_Service.enums.StatusType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        UUID accountNumber,
        AccountType accountType,
        BigDecimal balance,
        StatusType status) {
}

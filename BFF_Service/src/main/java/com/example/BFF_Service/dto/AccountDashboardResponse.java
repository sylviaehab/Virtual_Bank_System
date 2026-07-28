package com.example.BFF_Service.dto;

import com.example.BFF_Service.enums.AccountType;
import com.example.BFF_Service.enums.StatusType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AccountDashboardResponse(
        UUID accountId,
        UUID accountNumber,
        AccountType accountType,
        BigDecimal balance,
        StatusType status,
        List<TransactionResponse> transactions
) {
}

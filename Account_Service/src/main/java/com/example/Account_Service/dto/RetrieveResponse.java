package com.example.Account_Service.dto;

import com.example.Account_Service.enums.AccountType;
import com.example.Account_Service.enums.StatusType;

import java.math.BigDecimal;
import java.util.UUID;

public record RetrieveResponse(
        UUID accountId,
        UUID accountNumber,
        AccountType accountType,
        BigDecimal balance,
        StatusType status

) {
}

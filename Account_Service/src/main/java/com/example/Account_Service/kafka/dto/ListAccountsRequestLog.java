package com.example.Account_Service.kafka.dto;

import com.example.Account_Service.enums.AccountType;
import com.example.Account_Service.enums.StatusType;

public record ListAccountsRequestLog(
        AccountType accountType,
        StatusType status
) {
}

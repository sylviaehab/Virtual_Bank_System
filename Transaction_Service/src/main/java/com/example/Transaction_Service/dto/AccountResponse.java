package com.example.Transaction_Service.dto;

import java.math.BigDecimal;
import java.util.UUID;
import com.example.Transaction_Service.enums.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private UUID accountId;
    private UUID accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private StatusType status;
}
package com.example.Transaction_Service.dto;

import com.example.Transaction_Service.entity.TransactionStatus;
import com.example.Transaction_Service.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private String referenceNumber;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private TransactionStatus status;
    private String remarks;
    private LocalDateTime createdAt;
}
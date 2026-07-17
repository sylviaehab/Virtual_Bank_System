package com.example.Transaction_Service.service;

import com.example.Transaction_Service.dto.AccountBalanceResponse;
import com.example.Transaction_Service.dto.DepositRequest;
import com.example.Transaction_Service.dto.TransactionHistoryResponse;
import com.example.Transaction_Service.dto.TransactionResponse;
import com.example.Transaction_Service.dto.TransferRequest;
import com.example.Transaction_Service.dto.WithdrawRequest;


public interface TransactionService {

    TransactionResponse deposit(DepositRequest request);

    TransactionResponse withdraw(WithdrawRequest request);

    TransactionResponse transfer(TransferRequest request);

    TransactionHistoryResponse getTransactionHistory(String accountNumber, int page, int size);

    TransactionResponse getTransactionByReference(String referenceNumber);

}

package com.example.Transaction_Service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Transaction_Service.dto.TransactionHistoryResponse;
import com.example.Transaction_Service.dto.TransferExecutionRequest;
import com.example.Transaction_Service.dto.TransferInitiationRequest;
import com.example.Transaction_Service.dto.TransferResponse;
import com.example.Transaction_Service.scheduler.InterestScheduler;
import com.example.Transaction_Service.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
      private final InterestScheduler interestScheduler;

    /**
     * Initiates a fund transfer between two accounts.
     * Inserts a transaction row with status INITIATED; no money moves yet.
     */
    @PostMapping("/transactions/transfer/initiation")
    public ResponseEntity<TransferResponse> initiateTransfer(
            @Valid @RequestBody TransferInitiationRequest request) {
        TransferResponse response = transactionService.initiateTransfer(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Executes a previously initiated fund transfer: calls the Account Service
     * to debit/credit the two accounts, then updates the transaction to
     * SUCCESS or FAILED.
     */
    @PostMapping("/transactions/transfer/execution")
    public ResponseEntity<TransferResponse> executeTransfer(
            @Valid @RequestBody TransferExecutionRequest request) {
        TransferResponse response = transactionService.executeTransfer(request.getTransactionId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the transaction history for a specific account
     * (transactions where the account is either sender or receiver).
     */
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable UUID accountId) {
        List<TransactionHistoryResponse> history = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    
    /**
     * Manually triggers the daily interest job immediately, for testing
     * purposes only - so you don't have to wait until midnight to verify it
     * works end to end. In a real system this endpoint should be removed or
     * locked down (e.g. admin-only) before going to production.
     */
    @PostMapping("/transactions/interest/run-now")
    public ResponseEntity<String> runInterestJobNow() {
        interestScheduler.creditDailyInterest();
        return ResponseEntity.ok("Interest job triggered.");
    }
}

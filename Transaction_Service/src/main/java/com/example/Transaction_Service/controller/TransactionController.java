package com.example.Transaction_Service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.Transaction_Service.dto.DepositRequest;
import com.example.Transaction_Service.dto.TransactionHistoryResponse;
import com.example.Transaction_Service.dto.TransactionResponse;
import com.example.Transaction_Service.dto.TransferRequest;
import com.example.Transaction_Service.dto.WithdrawRequest;
import com.example.Transaction_Service.service.TransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")

public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
       TransactionResponse response = transactionService.deposit(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
   @PostMapping("/withdraw")
   public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
       TransactionResponse response = transactionService.withdraw(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response); 
    }
     @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
       TransactionResponse response = transactionService.transfer(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
     @GetMapping("/account/{accountNumber}")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        TransactionHistoryResponse response =
                transactionService.getTransactionHistory(accountNumber, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(
            @PathVariable String referenceNumber) {

        TransactionResponse response = transactionService.getTransactionByReference(referenceNumber);
        return ResponseEntity.ok(response);
    }
    
}

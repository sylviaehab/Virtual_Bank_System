package com.example.Account_Service.client;

import com.example.Account_Service.client.dto.TransactionHistoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "Transaction-Service")
public interface TransactionClient {
    @GetMapping("/accounts/{accountId}/transactions")
    public List<TransactionHistoryResponse> getTransactionHistory(
            @PathVariable UUID accountId);
}

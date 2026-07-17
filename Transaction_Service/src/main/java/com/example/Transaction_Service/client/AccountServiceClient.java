package com.example.Transaction_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.Transaction_Service.dto.AccountBalanceResponse;

@FeignClient(
        name = "account-service",
        url = "${account-service.url}",
        fallback = AccountServiceClientFallback.class
)
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountNumber}/balance")
    AccountBalanceResponse getAccountBalance(@PathVariable("accountNumber") String accountNumber);

}
package com.example.Transaction_Service.client;

import java.util.List;
import java.util.UUID;

import com.example.Transaction_Service.enums.AccountType;
import com.example.Transaction_Service.enums.StatusType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Transaction_Service.dto.AccountResponse;
import com.example.Transaction_Service.dto.AccountTransferRequest;

/**
 * "account-service" must exactly match the spring.application.name that the
 * Account Service registers under in Eureka. Feign + the Eureka client resolve
 * this logical name to a real host:port automatically - no manual URL needed.
 */
@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @PutMapping("/accounts/transfer")
    ResponseEntity<Void> transfer(@RequestBody AccountTransferRequest request);

    /**
     * Used to validate that an account exists (and optionally check its status)
     * before creating an INITIATED transaction row.
     */
    @GetMapping("/accounts/{accountId}")
    AccountResponse getAccount(@PathVariable("accountId") UUID accountId);


    @GetMapping("/accounts")
    List<AccountResponse> listAccounts(
            @RequestParam("accountType") AccountType accountType,
            @RequestParam("status") StatusType status);
}


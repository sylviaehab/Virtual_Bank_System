package com.example.Transaction_Service.client;

import com.example.Transaction_Service.dto.AccountBalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * TEMPORARY STUB — Account Service does not exist yet.
 * Activates whenever the real Feign call to Account Service fails
 * (connection refused, timeout, 5xx) — right now, that's ALWAYS,
 * since nothing is listening on account-service.url.
 *
 * Once Account Service's GET /api/accounts/{accountNumber}/balance
 * endpoint is live, this fallback stays in place (fallbacks are a
 * permanent resiliency pattern) but will only fire on genuine outages.
 */
@Slf4j
@Component
public class AccountServiceClientFallback implements AccountServiceClient {

    @Override
    public AccountBalanceResponse getAccountBalance(String accountNumber) {
        log.warn("Account Service is unavailable (stub fallback active). " +
                "Returning mock balance for account: {}", accountNumber);

        return AccountBalanceResponse.builder()
                .accountNumber(accountNumber)
                .balance(new BigDecimal("10000.00"))
                .active(true)
                .build();
    }

}
package com.example.Account_Service.config;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.Account_Service.Enum.AccountType;
import com.example.Account_Service.entity.Account;
import com.example.Account_Service.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemAccountInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        boolean alreadyExists = accountRepository
                .findFirstByAccountType(AccountType.SYSTEM)
                .isPresent();

        if (alreadyExists) {
            return;
        }

        Account systemAccount = new Account();
        // accountId is @GeneratedValue - do NOT set it manually
        systemAccount.setAccountType(AccountType.SYSTEM);
        systemAccount.setAccountNumber(UUID.randomUUID());
        systemAccount.setBalance(new BigDecimal("999999999.00"));
        systemAccount.setStatus("ACTIVE");
        // userId intentionally left null - this account has no owning user

        accountRepository.save(systemAccount);
    }
}
package com.example.Account_Service.service;

import com.example.Account_Service.ExceptionHandler.AccountNotFoundException;
import com.example.Account_Service.ExceptionHandler.CreateException;
import com.example.Account_Service.ExceptionHandler.UserNotFoundException;
import com.example.Account_Service.client.TransactionClient;
import com.example.Account_Service.client.UserClient;
import com.example.Account_Service.client.dto.UserResponse;
import com.example.Account_Service.dto.*;
import com.example.Account_Service.entity.Account;
import com.example.Account_Service.mapping.AccountMapper;
import com.example.Account_Service.repository.AccountRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserClient userClient;
    private final TransactionClient transactionClient;
    private final AccountMapper mapper;

    public AccountService(AccountRepository accountRepository,
                          UserClient userClient,
                          TransactionClient transactionClient,
                          AccountMapper mapper) {
        this.accountRepository = accountRepository;
        this.userClient = userClient;
        this.transactionClient = transactionClient;
        this.mapper = mapper;
    }

    public AccountCreateResponse addAccount(AccountRequest accountRequest) {
        try {
            UserResponse user = userClient.getUser(accountRequest.userId());
        } catch (FeignException ex) {
            throw new UserNotFoundException(
                    "User with ID " + accountRequest.userId().toString() + " not found"
            );
        }
        Account newAccount = mapper.toAccount(accountRequest);
        Account savedAccount = accountRepository.save(newAccount);

        return mapper.toCreateResponse(savedAccount);

    }

    public TransferResponse updateBalance(TransferRequest transferRequest) {
        Account fromAccount = accountRepository.findById(transferRequest.fromAccountId()).
                orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + transferRequest.fromAccountId().toString() + " not found"));

        Account toAccount = accountRepository.findById(transferRequest.toAccountId()).
                orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + transferRequest.toAccountId().toString() + " not found"));
        try {
            transactionClient.transfer(transferRequest);
        } catch (FeignException ex) {
            throw new CreateException("Invalid account type or initial balance.");
        }

        return new TransferResponse("Account updated successfully.");
    }

    public RetrieveResponse getAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + accountId.toString() + " not found"));

        return mapper.toRetrieveResponse(account);
    }

    public List<RetrieveResponse> getAllAccounts(UUID userId) {
        try {
            UserResponse user = userClient.getUser(userId);
        } catch (FeignException ex) {
            throw new UserNotFoundException(
                    "User with ID " + userId + " not found"
            );
        }
        List<RetrieveResponse> accounts = accountRepository.findAll().stream()
                .map(account -> mapper.toRetrieveResponse(account))
                .toList();

        return accounts;
    }
}

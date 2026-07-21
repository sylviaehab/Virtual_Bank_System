package com.example.Account_Service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Account_Service.Enum.AccountType;
import com.example.Account_Service.ExceptionHandler.AccountNotFoundException;
import com.example.Account_Service.ExceptionHandler.IllegalTransferException;
import com.example.Account_Service.ExceptionHandler.InsufficientBalanceException;
import com.example.Account_Service.ExceptionHandler.UserNotFoundException;
import com.example.Account_Service.client.UserClient;
import com.example.Account_Service.client.dto.TransferRequest;
import com.example.Account_Service.client.dto.UserResponse;
import com.example.Account_Service.dto.AccountCreateResponse;
import com.example.Account_Service.dto.AccountRequest;
import com.example.Account_Service.dto.RetrieveResponse;
import com.example.Account_Service.dto.TransferResponse;
import com.example.Account_Service.entity.Account;
import com.example.Account_Service.mapping.AccountMapper;
import com.example.Account_Service.repository.AccountRepository;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserClient userClient;
    private final AccountMapper mapper;

    public AccountService(AccountRepository accountRepository,
                          UserClient userClient,
                          AccountMapper mapper) {
        this.accountRepository = accountRepository;
        this.userClient = userClient;
        this.mapper = mapper;
    }

    public AccountCreateResponse addAccount(AccountRequest accountRequest) {

        UserResponse user = checkUser(accountRequest.userId());

        Account newAccount = mapper.toAccount(accountRequest);
        Account savedAccount = accountRepository.save(newAccount);

        return mapper.toCreateResponse(savedAccount);

    }

    @Transactional
    public TransferResponse updateBalance(TransferRequest transferRequest) {

        Account fromAccount = checkAccount(transferRequest.fromAccountId());
        Account toAccount = checkAccount(transferRequest.toAccountId());

        if (fromAccount.getAccountId().equals(toAccount.getAccountId())) {
            throw new IllegalTransferException("Transfer between the same account is not allowed.");
        }
        if (fromAccount.getBalance().compareTo(transferRequest.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferRequest.amount()));
        toAccount.setBalance(toAccount.getBalance().add(transferRequest.amount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return new TransferResponse("Account updated successfully.");
    }

    public RetrieveResponse getAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + accountId.toString() + " not found."));

        return mapper.toRetrieveResponse(account);
    }

    public List<RetrieveResponse> getAllAccounts(UUID userId) {

        UserResponse user = checkUser(userId);

        List<RetrieveResponse> accounts = accountRepository.findAll().stream()
                .map(account -> mapper.toRetrieveResponse(account))
                .toList();

        return accounts;
    }

    private Account checkAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + accountId.toString() + " not found."));


    }

    private UserResponse checkUser(UUID userId) {
        try {
            return userClient.getUser(userId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException(
                    "User with ID " + userId.toString() + " not found."
            );
        }

    }
    public List<RetrieveResponse> listAccounts(String accountType, String status) {
    List<Account> accounts;
    if (accountType != null && status != null) {
        AccountType type = AccountType.valueOf(accountType.toUpperCase());
        accounts = accountRepository.findByAccountTypeAndStatus(type, status);
    } else {
        accounts = accountRepository.findAll();
    }
    return accounts.stream()
            .map(mapper::toRetrieveResponse)
            .collect(Collectors.toList());
}
}
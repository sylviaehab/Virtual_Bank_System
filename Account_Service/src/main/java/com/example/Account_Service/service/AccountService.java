package com.example.Account_Service.service;

import com.example.Account_Service.client.TransactionClient;
import com.example.Account_Service.client.UserClient;
import com.example.Account_Service.client.dto.TransactionHistoryResponse;
import com.example.Account_Service.client.dto.TransferRequest;
import com.example.Account_Service.dto.AccountCreateResponse;
import com.example.Account_Service.dto.AccountRequest;
import com.example.Account_Service.dto.RetrieveResponse;
import com.example.Account_Service.dto.TransferResponse;
import com.example.Account_Service.entity.Account;
import com.example.Account_Service.enums.AccountType;
import com.example.Account_Service.enums.StatusType;
import com.example.Account_Service.exceptionHandler.*;
import com.example.Account_Service.kafka.Producer.KafkaLogProducer;
import com.example.Account_Service.kafka.dto.AccountRequestLog;
import com.example.Account_Service.kafka.dto.DownstreamFailureResponse;
import com.example.Account_Service.kafka.dto.ListAccountsRequestLog;
import com.example.Account_Service.kafka.dto.UserAccountsRequestLog;
import com.example.Account_Service.kafka.mapper.KafkaMapper;
import com.example.Account_Service.mapping.AccountMapper;
import com.example.Account_Service.repository.AccountRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserClient userClient;
    private final TransactionClient transactionClient;
    private final AccountMapper mapper;
    private final KafkaLogProducer kafkaLogProducer;
    private final KafkaMapper kafkaMapper;

    public AccountCreateResponse addAccount(AccountRequest accountRequest) {
        log.info("Creating account for user: {}", accountRequest.userId());

        kafkaLogProducer.sendRequest(accountRequest);

        checkUser(accountRequest.userId());

        Account newAccount = mapper.toAccount(accountRequest);
        Account savedAccount = accountRepository.save(newAccount);

        AccountCreateResponse accountResponse = mapper.toCreateResponse(savedAccount);

        kafkaLogProducer.sendResponse(accountResponse);


        log.info(
                "Account {} created successfully.",
                savedAccount.getAccountId()
        );

        return accountResponse;

    }

    @Transactional
    public TransferResponse updateBalance(TransferRequest transferRequest) {
        log.info(
                "Starting transfer from {} to {}",
                transferRequest.fromAccountId(),
                transferRequest.toAccountId()
        );

        kafkaLogProducer.sendRequest(transferRequest);

        Account fromAccount = checkAccount(transferRequest.fromAccountId());
        Account toAccount = checkAccount(transferRequest.toAccountId());

        if (fromAccount.getAccountId().equals(toAccount.getAccountId())) {
            throw new IllegalTransferException("Transfer between the same account is not allowed.");
        }
        if (fromAccount.getStatus().equals(StatusType.INACTIVE)) {
            throw new InactiveAccountException("From Account is INACTIVE");
        }
        if (fromAccount.getBalance().compareTo(transferRequest.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferRequest.amount()));
        toAccount.setBalance(toAccount.getBalance().add(transferRequest.amount()));

        TransferResponse response =
                new TransferResponse("Accounts: " + fromAccount.getAccountId() + " and " + toAccount.getAccountId() + "  updated successfully.");

        kafkaLogProducer.sendResponse(response);
        log.info(
                "Transfer completed successfully from {} to {}",
                transferRequest.fromAccountId(),
                transferRequest.toAccountId()
        );
        return response;
    }

    public RetrieveResponse getAccount(UUID accountId) {
        log.info("Retrieving account with id: {}", accountId);

        kafkaLogProducer.sendRequest(
                new AccountRequestLog(accountId)
        );

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + accountId + " not found."));

        RetrieveResponse response = mapper.toRetrieveResponse(account);

        kafkaLogProducer.sendResponse(response);

        log.info("Account {} retrieved successfully.", accountId);

        return response;
    }

    public List<RetrieveResponse> getAllAccounts(UUID userId) {

        checkUser(userId);

        return accountRepository.findAll().stream()
                .map(mapper::toRetrieveResponse)
                .toList();
    }

    private Account checkAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account with ID " + accountId + " not found."));


    }

    private void checkUser(UUID userId) {
        try {
            userClient.getUser(userId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException(
                    "User with ID " + userId.toString() + " not found."
            );
        } catch (FeignException ex) {
            kafkaLogProducer.sendResponse(
                    new DownstreamFailureResponse(
                            "User Service",
                            "Failed to retrieve user data from User Service."
                    )
            );
            throw new DownstreamServiceException(
                    "Failed to retrieve user data from User Service."
            );
        }

    }

    public List<RetrieveResponse> getUserAccounts(UUID userId) {
        log.info("Retrieving accounts for user: {}", userId);

        kafkaLogProducer.sendRequest(
                new UserAccountsRequestLog(userId)
        );

        checkUser(userId);
        List<RetrieveResponse> accounts = accountRepository.findByUserId(userId).stream()
                .map(mapper::toRetrieveResponse)
                .toList();
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for user ID " + userId);
        }
        kafkaLogProducer.sendResponse(accounts);

        log.info(
                "Retrieved {} accounts for user: {}",
                accounts.size(),
                userId
        );
        return accounts;
    }

    public List<RetrieveResponse> listAccounts(AccountType accountType, StatusType status) {

        log.info(
                "Retrieving accounts with filters - type: {}, status: {}",
                accountType,
                status
        );

        kafkaLogProducer.sendRequest(
                new ListAccountsRequestLog(accountType, status)
        );


        List<Account> accounts;
        if (accountType != null && status != null) {
            accounts = accountRepository.findByAccountTypeAndStatus(accountType, status);
        } else {
            accounts = accountRepository.findAll();
        }
        List<RetrieveResponse> response = accounts.stream()
                .map(mapper::toRetrieveResponse)
                .collect(Collectors.toList());

        kafkaLogProducer.sendResponse(response);

        log.info(
                "Retrieved {} accounts.",
                response.size()
        );

        return response;
    }

    public int updateInactiveAccounts() {
        log.info("Starting inactive accounts check");
        int updatedAccounts = 0;
        Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
        List<Account> activeAccounts = accountRepository.findByStatus(StatusType.ACTIVE);

        log.info("Found {} active accounts to check",
                activeAccounts.size());

        for (Account account : activeAccounts) {

            log.debug("Checking account with id: {}",
                    account.getAccountId());
            try {
                List<TransactionHistoryResponse> transactions =
                        transactionClient.getTransactionHistory(account.getAccountId());

                Optional<TransactionHistoryResponse> latestTransaction =
                        transactions.stream()
                                .max(Comparator.comparing(TransactionHistoryResponse::timestamp));

                if (latestTransaction.isEmpty()) {
                    log.debug("No transactions found for account {}",
                            account.getAccountId());
                    continue;
                }
                log.debug(
                        "Latest transaction for account {} was at {}",
                        account.getAccountId(),
                        latestTransaction.get().timestamp()
                );

                if (latestTransaction.get().timestamp().isBefore(oneDayAgo)) {
                    account.setStatus(StatusType.INACTIVE);
                    accountRepository.save(account);
                    updatedAccounts++;
                    log.info(
                            "Account {} changed from ACTIVE to INACTIVE",
                            account.getAccountId()
                    );
                }
            } catch (FeignException ex) {
                kafkaLogProducer.sendResponse(
                        new DownstreamFailureResponse(
                                "Transaction Service",
                                "Failed to retrieve transactions."
                        )
                );
                log.error(
                        "Failed to retrieve transactions for account {}",
                        account.getAccountId(),
                        ex
                );
            }

        }
        log.info(
                "Inactive accounts check completed. {} accounts updated.",
                updatedAccounts
        );

        return updatedAccounts;
    }
}
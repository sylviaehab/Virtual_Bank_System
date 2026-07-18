package com.example.Transaction_Service.service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.Transaction_Service.client.AccountServiceClient;
import com.example.Transaction_Service.dto.AccountBalanceResponse;
import com.example.Transaction_Service.dto.DepositRequest;
import com.example.Transaction_Service.dto.TransactionHistoryResponse;
import com.example.Transaction_Service.dto.TransactionResponse;
import com.example.Transaction_Service.dto.TransferRequest;
import com.example.Transaction_Service.dto.WithdrawRequest;
import com.example.Transaction_Service.entity.Transaction;
import com.example.Transaction_Service.entity.TransactionStatus;
import com.example.Transaction_Service.entity.TransactionType;
import com.example.Transaction_Service.exception.InsufficientBalanceException;
import com.example.Transaction_Service.exception.InvalidTransferException;
import com.example.Transaction_Service.exception.TransactionNotFoundException;
import com.example.Transaction_Service.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
@Slf4j

@Setter
@Getter
@Service
public class TransactionServiceImpl implements TransactionService {

      private final TransactionRepository transactionRepository;
      private final AccountServiceClient accountServiceClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountServiceClient accountServiceClient) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        log.info("Processing deposit request: {}", request.getAccountNumber());
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .destinationAccountNumber(request.getAccountNumber())
                .sourceAccountNumber(null)
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Deposit successful. Reference number: {}", savedTransaction.getReferenceNumber());

        return mapToResponse(savedTransaction);
    }

   
    @Override
    @Transactional
    
    public TransactionResponse transfer(TransferRequest request) {

        log.info("Processing transfer request from {} to {}",
                request.getSourceAccountNumber(), request.getDestinationAccountNumber());

        // Guard clause: cannot transfer to yourself
        if (request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())) {
            log.warn("Transfer rejected — source and destination accounts are identical: {}",
                    request.getSourceAccountNumber());
            throw new InvalidTransferException("Source and destination accounts must be different");
        }

        // Step 1: validate source account has sufficient funds
       AccountBalanceResponse sourceAccount =
                accountServiceClient.getAccountBalance(request.getSourceAccountNumber());
      validateSufficientFunds(sourceAccount, request.getAmount());

        // Step 2: validate destination account exists and is active
       AccountBalanceResponse destinationAccount =
             accountServiceClient.getAccountBalance(request.getDestinationAccountNumber());
        validateDestinationAccount(destinationAccount);

        // Step 3: persist the transaction record (our local, atomic write)
        String referenceNumber = generateReferenceNumber();

        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .sourceAccountNumber(request.getSourceAccountNumber())
                .destinationAccountNumber(request.getDestinationAccountNumber())
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // NOTE: Once Account Service exists, the ACTUAL balance debit/credit calls
        // will happen here, inside this same method. If either call fails AFTER this
        // save() has committed, @Transactional on THIS method cannot undo a successful
        // call already made to Account Service (a separate service, separate database).
        // This distributed-transaction gap will be addressed explicitly with a
        // Saga/compensating-transaction step once Account Service is live.

        log.info("Transfer successful. Reference number: {}", savedTransaction.getReferenceNumber());

        return mapToResponse(savedTransaction);
    }

     @Override
    public TransactionHistoryResponse getTransactionHistory(String accountNumber, int page, int size) {

        log.info("Fetching transaction history for account: {} (page={}, size={})", accountNumber, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> transactionPage = transactionRepository
                .findBySourceAccountNumberOrDestinationAccountNumber(accountNumber, accountNumber, pageable);

        List<TransactionResponse> transactionResponses = transactionPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return TransactionHistoryResponse.builder()
                .transactions(transactionResponses)
                .currentPage(transactionPage.getNumber())
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .isLastPage(transactionPage.isLast())
                .build();
    }

    @Override
    public TransactionResponse getTransactionByReference(String referenceNumber) {

        log.info("Fetching transaction by reference number: {}", referenceNumber);

        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with reference number: " + referenceNumber
                ));

        return mapToResponse(transaction);
    }
    private String generateReferenceNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN-" + datePart + "-" + randomPart;
    }

    @Override
    @SuppressWarnings("null")
    public TransactionResponse withdraw(WithdrawRequest request) {

        log.info("Processing withdrawal request for account: {}", request.getAccountNumber());

        AccountBalanceResponse account = accountServiceClient.getAccountBalance(request.getAccountNumber());

        validateSufficientFunds(account, request.getAmount());

        String referenceNumber = generateReferenceNumber();

        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .sourceAccountNumber(request.getAccountNumber())
                .destinationAccountNumber(null)
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Withdrawal successful. Reference number: {}", savedTransaction.getReferenceNumber());

        return mapToResponse(savedTransaction);
    }
    private void validateSufficientFunds(AccountBalanceResponse account, BigDecimal requestedAmount) {

        if (!account.isActive()) {
            log.warn("Operation rejected — account {} is not active", account.getAccountNumber());
            throw new InsufficientBalanceException(
                    "Account " + account.getAccountNumber() + " is not active"
            );
        }

        if (account.getBalance().compareTo(requestedAmount) < 0) {
            log.warn("Operation rejected — insufficient balance for account {}. Available: {}, Requested: {}",
                    account.getAccountNumber(), account.getBalance(), requestedAmount);
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + account.getBalance() + ", Requested: " + requestedAmount
            );
        }
    }

    private void validateDestinationAccount(AccountBalanceResponse account) {
        if (!account.isActive()) {
            log.warn("Transfer rejected — destination account {} is not active", account.getAccountNumber());
            throw new InvalidTransferException(
                    "Destination account " + account.getAccountNumber() + " is not active"
            );
        }
    }
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .sourceAccountNumber(transaction.getSourceAccountNumber())
                .destinationAccountNumber(transaction.getDestinationAccountNumber())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}

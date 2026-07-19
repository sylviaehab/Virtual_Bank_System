package com.example.Transaction_Service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Transaction_Service.client.AccountServiceClient;
import com.example.Transaction_Service.dto.AccountTransferRequest;
import com.example.Transaction_Service.dto.TransactionHistoryResponse;
import com.example.Transaction_Service.dto.TransferInitiationRequest;
import com.example.Transaction_Service.dto.TransferResponse;
import com.example.Transaction_Service.entity.Transaction;
import com.example.Transaction_Service.entity.TransactionStatus;
import com.example.Transaction_Service.exception.BadRequestException;
import com.example.Transaction_Service.exception.ResourceNotFoundException;
import com.example.Transaction_Service.repository.TransactionRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j

public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;

    /**
     * POST /transactions/transfer/initiation
     * Records a new transaction row with status INITIATED. No money moves yet.
     */
    @Transactional
    public TransferResponse initiateTransfer(TransferInitiationRequest request) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BadRequestException("Invalid 'from' or 'to' account ID.");
        }

        validateAccountExists(request.getFromAccountId());
        validateAccountExists(request.getToAccountId());

        Transaction transaction = Transaction.builder()
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(TransactionStatus.INITIATED)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction {} initiated: {} -> {} amount {}",
                saved.getTransactionId(), saved.getFromAccountId(), saved.getToAccountId(), saved.getAmount());

        return TransferResponse.builder()
                .transactionId(saved.getTransactionId())
                .status("Initiated")
                .timestamp(saved.getCreatedAt())
                .build();
    }

    /**
     * POST /transactions/transfer/execution
     * Looks up the INITIATED transaction, calls the Account Service (via Feign)
     * to debit/credit the two accounts, then flips the transaction to
     * SUCCESS or FAILED.
     */
    @Transactional
    public TransferResponse executeTransfer(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction with ID " + transactionId + " not found."));

        if (transaction.getStatus() != TransactionStatus.INITIATED) {
            throw new BadRequestException(
                    "Transaction " + transactionId + " is not in an INITIATED state.");
        }

        try {
            accountServiceClient.transfer(new AccountTransferRequest(
                    transaction.getFromAccountId(),
                    transaction.getToAccountId(),
                    transaction.getAmount()));

            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (FeignException.BadRequest ex) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            log.warn("Account Service rejected transfer for transaction {}: {}",
                    transactionId, ex.getMessage());
            throw new BadRequestException("Invalid 'from' or 'to' account ID, or insufficient funds.");
        } catch (Exception ex) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw ex;
        }

        Transaction updated = transactionRepository.save(transaction);
        log.info("Transaction {} executed with status {}", updated.getTransactionId(), updated.getStatus());

        return TransferResponse.builder()
                .transactionId(updated.getTransactionId())
                .status(capitalize(updated.getStatus().name()))
                .timestamp(updated.getUpdatedAt())
                .build();
    }

    /**
     * GET /accounts/{accountId}/transactions
     * Returns every transaction where the account is either sender or receiver.
     * Per the spec: amount is negative when the queried account was the sender
     * (money went out) and positive when it was the receiver (money came in).
     * "deliveryStatus" mirrors the internal TransactionStatus but uses the
     * spec's own vocabulary (SENT/DELIVERED/FAILED) instead of the raw enum.
     */
    public List<TransactionHistoryResponse> getTransactionHistory(UUID accountId) {
        List<Transaction> transactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(accountId, accountId);

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No transactions found for account ID " + accountId + ".");
        }

        return transactions.stream()
                .map(t -> {
                    boolean isSender = t.getFromAccountId().equals(accountId);
                    BigDecimal signedAmount = isSender ? t.getAmount().negate() : t.getAmount();

                    return TransactionHistoryResponse.builder()
                            .transactionId(t.getTransactionId())
                            .fromAccountId(t.getFromAccountId())
                            .toAccountId(t.getToAccountId())
                            .amount(signedAmount)
                            .description(t.getDescription())
                            .deliveryStatus(toDeliveryStatus(t.getStatus()))
                            .timestamp(t.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Confirms an account exists in the Account Service before we let a
     * transfer be initiated against it. A 404 from the Account Service is
     * translated into the same "Invalid 'from' or 'to' account ID." error
     * the spec expects from this endpoint.
     */
    private void validateAccountExists(UUID accountId) {
        try {
            accountServiceClient.getAccount(accountId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Invalid 'from' or 'to' account ID.");
        }
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.charAt(0) + value.substring(1).toLowerCase();
    }

    /**
     * Maps the internal TransactionStatus to the vocabulary used in the
     * transaction-history response per the spec (SENT / DELIVERED / FAILED).
     * INITIATED -> "SENT" (the transfer request has gone out but not settled)
     * SUCCESS   -> "DELIVERED" (funds have moved successfully)
     * FAILED    -> "FAILED"
     */
    private String toDeliveryStatus(TransactionStatus status) {
        switch (status) {
            case INITIATED:
                return "SENT";
            case SUCCESS:
                return "DELIVERED";
            case FAILED:
            default:
                return "FAILED";
        }
    }
}

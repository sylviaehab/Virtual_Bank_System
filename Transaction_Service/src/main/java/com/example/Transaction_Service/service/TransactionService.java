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
import com.example.Transaction_Service.kafka.KafkaProducerService;
import com.example.Transaction_Service.repository.TransactionRepository;
import com.example.Transaction_Service.service.TransactionStatusService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final TransactionStatusService transactionStatusService;


    /**
     * POST /transactions/transfer/initiation
     * Creates transaction with INITIATED status.
     */
    @Transactional
    public TransferResponse initiateTransfer(TransferInitiationRequest request) {

        kafkaProducerService.sendLog(
                "Transfer initiation requested. "
                        + "From=" + request.getFromAccountId()
                        + ", To=" + request.getToAccountId()
                        + ", Amount=" + request.getAmount(),
                "Request"
        );


        if (request.getFromAccountId().equals(request.getToAccountId())) {

            kafkaProducerService.sendLog(
                    "Transfer initiation failed. Reason: Same source and destination account.",
                    "Response"
            );

            throw new BadRequestException(
                    "Invalid 'from' or 'to' account ID.");
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


        kafkaProducerService.sendLog(
                "Transaction initiated successfully. "
                        + "TransactionId=" + saved.getTransactionId()
                        + ", From=" + saved.getFromAccountId()
                        + ", To=" + saved.getToAccountId()
                        + ", Amount=" + saved.getAmount(),
                "Response"
        );


        log.info("Transaction {} initiated: {} -> {} amount {}",
                saved.getTransactionId(),
                saved.getFromAccountId(),
                saved.getToAccountId(),
                saved.getAmount());


        return TransferResponse.builder()
                .transactionId(saved.getTransactionId())
                .status("Initiated")
                .timestamp(saved.getCreatedAt())
                .build();
    }



    /**
     * POST /transactions/transfer/execution
     */
    @Transactional
    public TransferResponse executeTransfer(UUID transactionId) {


        kafkaProducerService.sendLog(
                "Transfer execution requested. TransactionId=" + transactionId,
                "Request"
        );


        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction with ID " + transactionId + " not found."));


        if (transaction.getStatus() != TransactionStatus.INITIATED) {

            kafkaProducerService.sendLog(
                    "Transfer execution failed. Transaction is not INITIATED. "
                            + "TransactionId=" + transactionId,
                    "Response"
            );

            throw new BadRequestException(
                    "Transaction " + transactionId
                            + " is not in an INITIATED state.");
        }



        try {


            accountServiceClient.transfer(
                    new AccountTransferRequest(
                            transaction.getFromAccountId(),
                            transaction.getToAccountId(),
                            transaction.getAmount()
                    )
            );


            transaction.setStatus(TransactionStatus.SUCCESS);



        } catch (FeignException.BadRequest ex) {

            // Marked in a SEPARATE transaction (REQUIRES_NEW) so this FAILED
            // status survives even though this method's own @Transactional
            // will roll back everything else once BadRequestException is thrown.
           transactionStatusService.markTransactionFailed(transactionId);

            kafkaProducerService.sendLog(
                    "Transaction failed. "
                            + "TransactionId=" + transactionId
                            + ", Reason=" + ex.getMessage(),
                    "Response"
            );


            throw new BadRequestException(
                    "Invalid 'from' or 'to' account ID, or insufficient funds.");



        } catch (Exception ex) {

            transactionStatusService.markTransactionFailed(transactionId);

            kafkaProducerService.sendLog(
                    "Transaction failed. "
                            + "TransactionId=" + transactionId
                            + ", Reason=" + ex.getMessage(),
                    "Response"
            );


            throw ex;
        }



        Transaction updated = transactionRepository.save(transaction);



        kafkaProducerService.sendLog(
                "Transaction executed successfully. "
                        + "TransactionId=" + updated.getTransactionId()
                        + ", Amount=" + updated.getAmount()
                        + ", Status=" + updated.getStatus(),
                "Response"
        );



        log.info("Transaction {} executed with status {}",
                updated.getTransactionId(),
                updated.getStatus());



        return TransferResponse.builder()
                .transactionId(updated.getTransactionId())
                .status(capitalize(updated.getStatus().name()))
                .timestamp(updated.getUpdatedAt())
                .build();
    }


  
  



    /**
     * GET /accounts/{accountId}/transactions
     */
    public List<TransactionHistoryResponse> getTransactionHistory(UUID accountId) {


        List<Transaction> transactions =
                transactionRepository
                        .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
                                accountId,
                                accountId);



        if (transactions.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No transactions found for account ID "
                            + accountId + ".");
        }



        return transactions.stream()
                .map(t -> {


                    boolean isSender =
                            t.getFromAccountId().equals(accountId);



                    BigDecimal signedAmount =
                            isSender
                                    ? t.getAmount().negate()
                                    : t.getAmount();



                    return TransactionHistoryResponse.builder()
                            .transactionId(t.getTransactionId())
                            .fromAccountId(t.getFromAccountId())
                            .toAccountId(t.getToAccountId())
                            .amount(signedAmount)
                            .description(t.getDescription())
                            .deliveryStatus(
                                    toDeliveryStatus(t.getStatus()))
                            .timestamp(t.getCreatedAt())
                            .build();

                })
                .collect(Collectors.toList());
    }




    /**
     * Checks account existence using Account Service.
     */
    private void validateAccountExists(UUID accountId) {

        try {

            accountServiceClient.getAccount(accountId);

        } catch (FeignException.NotFound ex) {

            throw new BadRequestException(
                    "Invalid 'from' or 'to' account ID.");
        }
    }




    private String capitalize(String value) {

        if (value == null || value.isEmpty()) {
            return value;
        }

        return value.charAt(0)
                + value.substring(1).toLowerCase();
    }




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
package com.example.Transaction_Service.scheduler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.Transaction_Service.client.AccountServiceClient;
import com.example.Transaction_Service.dto.AccountResponse;
import com.example.Transaction_Service.dto.AccountTransferRequest;
import com.example.Transaction_Service.entity.Transaction;
import com.example.Transaction_Service.entity.TransactionStatus;
import com.example.Transaction_Service.enums.AccountType;
import com.example.Transaction_Service.enums.StatusType;
import com.example.Transaction_Service.kafka.KafkaProducerService;
import com.example.Transaction_Service.repository.TransactionRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestScheduler {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final KafkaProducerService kafkaProducerService;

    @Value("${interest.rate}")
    private BigDecimal interestRate;

    @Scheduled(cron = "${interest.cron}")
    public void creditDailyInterest() {

        log.info("Starting daily interest job at rate {}", interestRate);

        UUID systemAccountId = resolveSystemAccountId();

        if (systemAccountId == null) {

            log.error("No active SYSTEM account found in Account Service. Skipping interest job run.");

            kafkaProducerService.sendLog(
                    "INTEREST_SCHEDULER_FAILED | No SYSTEM account found."
            );

            return;
        }

        List<AccountResponse> savingsAccounts = fetchActiveSavingsAccounts();

        log.info("Using system account {} to credit {} active savings accounts",
                systemAccountId,
                savingsAccounts.size());

        for (AccountResponse account : savingsAccounts) {

            try {

                creditInterestForAccount(systemAccountId, account);

            } catch (Exception ex) {

                kafkaProducerService.sendLog(
                        "INTEREST_TRANSFER_FAILED | account="
                                + account.getAccountId()
                                + " | reason=" + ex.getMessage()
                );

                log.error("Failed to credit interest for account {}",
                        account.getAccountId(),
                        ex);
            }
        }

        log.info("Daily interest job completed.");

        kafkaProducerService.sendLog(
                "INTEREST_SCHEDULER_COMPLETED | processedAccounts="
                        + savingsAccounts.size()
        );
    }

    private UUID resolveSystemAccountId() {

        try {

            List<AccountResponse> systemAccounts =
                    accountServiceClient.listAccounts(AccountType.SYSTEM, StatusType.ACTIVE);

            if (systemAccounts == null || systemAccounts.isEmpty()) {
                return null;
            }

            return systemAccounts.get(0).getAccountId();

        } catch (Exception ex) {

            log.error("Failed to fetch SYSTEM account from Account Service", ex);

            return null;
        }
    }

    private List<AccountResponse> fetchActiveSavingsAccounts() {

        try {

            List<AccountResponse> accounts =
                    accountServiceClient.listAccounts(AccountType.SAVINGS, StatusType.ACTIVE);

            return accounts == null
                    ? Collections.emptyList()
                    : accounts;

        } catch (Exception ex) {

            log.error("Failed to fetch active savings accounts from Account Service", ex);

            return Collections.emptyList();
        }
    }

    @Transactional
    protected void creditInterestForAccount(UUID systemAccountId,
                                            AccountResponse account) {

        BigDecimal interestAmount = account.getBalance()
                .multiply(interestRate)
                .setScale(2, RoundingMode.HALF_UP);

        if (interestAmount.compareTo(BigDecimal.ZERO) <= 0) {

            log.debug("Skipping account {} - computed interest amount is zero.",
                    account.getAccountId());

            return;
        }

        Transaction transaction = Transaction.builder()
                .fromAccountId(systemAccountId)
                .toAccountId(account.getAccountId())
                .amount(interestAmount)
                .description("Daily interest credit")
                .status(TransactionStatus.INITIATED)
                .build();

        transaction = transactionRepository.save(transaction);

        try {

            accountServiceClient.transfer(
                    new AccountTransferRequest(
                            systemAccountId,
                            account.getAccountId(),
                            interestAmount));

            transaction.setStatus(TransactionStatus.SUCCESS);

        } catch (FeignException ex) {

            transaction.setStatus(TransactionStatus.FAILED);

            kafkaProducerService.sendLog(
                    "INTEREST_TRANSFER_FAILED | account="
                            + account.getAccountId()
                            + " | reason=" + ex.getMessage()
            );

            log.warn("Interest transfer failed for account {}: {}",
                    account.getAccountId(),
                    ex.getMessage());

        } finally {

            transactionRepository.save(transaction);
        }
    }
}
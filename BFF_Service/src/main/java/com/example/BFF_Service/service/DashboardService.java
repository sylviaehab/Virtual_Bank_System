package com.example.BFF_Service.service;

import com.example.BFF_Service.client.AccountClient;
import com.example.BFF_Service.client.TransactionClient;
import com.example.BFF_Service.client.UserClient;
import com.example.BFF_Service.dto.*;
import com.example.BFF_Service.exceptionHandler.DownstreamServiceException;
import com.example.BFF_Service.exceptionHandler.UserNotFoundException;
import com.example.BFF_Service.kafka.Producer.KafkaLogProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final UserClient userClient;
    private final AccountClient accountClient;
    private final TransactionClient transactionClient;
    private final KafkaLogProducer kafkaLogProducer;

    public Mono<DashboardResponse> getDashboard(UUID userId) {
        log.info("Starting dashboard aggregation for user: {}", userId);
        kafkaLogProducer.sendRequest(
                new DashboardRequest(userId)
        );
        Mono<UserResponse> user =
                userClient.getUserProfile(userId)
                        .onErrorMap(
                                WebClientResponseException.NotFound.class,
                                ex -> new UserNotFoundException(
                                        "User with Id " + userId + " Not Found."
                                )
                        )
                        .onErrorMap(
                                ex -> !(ex instanceof UserNotFoundException),
                                ex -> {
                                    log.error("Failed to retrieve user data.", ex);

                                    kafkaLogProducer.sendResponse(
                                            new DownstreamFailureResponse(
                                                    "User Service",
                                                    ex.getMessage()
                                            )
                                    );

                                    return new DownstreamServiceException(
                                            "Failed to retrieve user data."
                                    );
                                }
                        );

        Mono<List<AccountResponse>> accounts =
                accountClient.getUserAccounts(userId)
                        .collectList()
                        .doOnSuccess(accountList ->
                                log.info(
                                        "Retrieved {} accounts for user: {}",
                                        accountList.size(),
                                        userId
                                )
                        ).onErrorResume(
                                WebClientResponseException.NotFound.class,
                                ex -> {
                                    log.info(
                                            "No accounts found for user: {}",
                                            userId
                                    );
                                    return Mono.just(List.of());
                                }
                        )
                        .onErrorMap(ex -> {
                            log.error("Failed to retrieve accounts  data.", ex);

                            kafkaLogProducer.sendResponse(
                                    new DownstreamFailureResponse(
                                            "Account Service",
                                            ex.getMessage()
                                    )
                            );

                            return new DownstreamServiceException(
                                    "Failed to retrieve accounts data due to an issue with downstream services."
                            );
                        });

        return Mono.zip(user, accounts)
                .flatMap(tuple -> {
                    UserResponse userProfile = tuple.getT1();
                    List<AccountResponse> userAccounts = tuple.getT2();

                    log.info(
                            "Starting transaction aggregation for {} accounts of user: {}",
                            userAccounts.size(),
                            userId
                    );

                    return Flux.fromIterable(userAccounts)
                            .flatMap(this::buildAccountDashboard)
                            .collectList()
                            .map(accountsWithTransactions -> {
                                        log.info("Dashboard aggregation completed for user: {}", userId);

                                        DashboardResponse dashboardResponse =
                                                new DashboardResponse(
                                                        userProfile.userId(),
                                                        userProfile.username(),
                                                        userProfile.email(),
                                                        userProfile.firstName(),
                                                        userProfile.lastName(),
                                                        accountsWithTransactions);
                                        kafkaLogProducer.sendResponse(dashboardResponse);

                                        return dashboardResponse;
                                    }
                            );

                });

    }

    public Mono<AccountDashboardResponse> buildAccountDashboard(AccountResponse account) {

        log.debug(
                "Retrieving transactions for account: {}",
                account.accountId()
        );

        return transactionClient
                .getAccountTransaction(account.accountId())
                .collectList()
                .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        ex -> {
                            log.debug(
                                    "No transactions found for account: {}",
                                    account.accountId()
                            );

                            return Mono.just(List.of());
                        }
                ).map(transactions -> {
                    log.debug(
                            "Retrieved {} transactions for account: {}",
                            transactions.size(),
                            account.accountId()
                    );
                    return new AccountDashboardResponse(
                            account.accountId(),
                            account.accountNumber(),
                            account.accountType(),
                            account.balance(),
                            account.status(),
                            transactions);
                })
                .onErrorMap(ex -> {
                    log.error(
                            "Failed to retrieve transactions for account: {}",
                            account.accountId(),
                            ex
                    );
                    kafkaLogProducer.sendResponse(
                            new DownstreamFailureResponse(
                                    "Transaction Service",
                                    ex.getMessage()
                            )
                    );

                    return new DownstreamServiceException(
                            "Failed to retrieve transactions data for account id " +
                                    account.accountId() +
                                    " due to an issue with downstream services."
                    );
                });

    }

}

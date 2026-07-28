package com.example.BFF_Service.client;

import com.example.BFF_Service.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionClient {
    private final WebClient.Builder webClientBuilder;

    public Flux<TransactionResponse> getAccountTransaction(UUID accountId) {
        return webClientBuilder
                .build()
                .get()
                .uri("http://TRANSACTION-SERVICE/accounts/{accountId}/transactions",
                        accountId
                )
                .retrieve()
                .bodyToFlux(TransactionResponse.class);
    }
}

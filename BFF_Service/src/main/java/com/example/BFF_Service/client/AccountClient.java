package com.example.BFF_Service.client;

import com.example.BFF_Service.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountClient {
    private final WebClient.Builder webClientBuilder;

    public Flux<AccountResponse> getUserAccounts(UUID userId) {
        return webClientBuilder
                .build()
                .get()
                .uri("http://ACCOUNT-SERVICE/accounts/users/{userId}",
                        userId
                )
                .retrieve()
                .bodyToFlux(AccountResponse.class);
    }
}

package com.example.BFF_Service.client;

import com.example.BFF_Service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient.Builder webClientBuilder;

    public Mono<UserResponse> getUserProfile(UUID userId) {
        return webClientBuilder
                .build()
                .get()
                .uri("http://USER-SERVICE/users/{userId}/profile",
                        userId
                )
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}

package com.example.BFF_Service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {
    @Bean
    public OpenAPI accountServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Virtual Bank BFF API")
                        .description("Backend For Frontend API for aggregating user, account, and transaction data."
                        ));
    }
}

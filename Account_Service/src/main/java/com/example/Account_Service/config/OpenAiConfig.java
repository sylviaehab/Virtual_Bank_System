package com.example.Account_Service.config;

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
                        .title("Account Service API")
                        .description("REST APIs for Account Microservice"));
    }
}

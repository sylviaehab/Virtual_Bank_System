package com.User_Service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Virtual Bank - User Service API",
                version = "1.0",
                description = """
                        User Service for the Virtual Bank System.
                        
                        Responsibilities:
                        - User registration
                        - User login
                        - User profile retrieval
                        """
        )
)
public class OpenApiConfig {
}
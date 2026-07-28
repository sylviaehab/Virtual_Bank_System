package com.example.BFF_Service.kafka.mapper;

import com.example.BFF_Service.exceptionHandler.KafkaLoggingException;
import com.example.BFF_Service.kafka.dto.LogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KafkaMapper {
    private final ObjectMapper objectMapper;

    public String toRequestLog(Object request) {

        try {

            LogMessage logMessage = new LogMessage(
                    objectMapper.writeValueAsString(request),
                    "Request",
                    Instant.now()
            );

            return objectMapper.writeValueAsString(logMessage);

        } catch (JacksonException ex) {

            throw new KafkaLoggingException(
                    "Failed to serialize request log."
            );
        }
    }

    public String toResponseLog(Object response) {

        try {

            LogMessage logMessage = new LogMessage(
                    objectMapper.writeValueAsString(response),
                    "Response",
                    Instant.now()
            );

            return objectMapper.writeValueAsString(logMessage);

        } catch (JacksonException ex) {

            throw new KafkaLoggingException(
                    "Failed to serialize response log."
            );
        }
    }

}

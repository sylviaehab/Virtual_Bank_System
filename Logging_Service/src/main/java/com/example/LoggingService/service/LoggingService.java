package com.example.LoggingService.service;


import com.example.LoggingService.dto.LogMessage;
import com.example.LoggingService.entity.LogEntry;
import com.example.LoggingService.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
//converts the Kafka DTO into a JPA entity and saves it
@Service
public class LoggingService {

    private final LogRepository logRepository;

    public LoggingService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public LogEntry save(LogMessage message) {
        LogEntry entry = LogEntry.builder()
                .serviceName(message.serviceName())
                .messageType(message.messageType())
                .message(message.message())
                .dateTime(
                        message.dateTime() != null
                                ? message.dateTime()
                                : Instant.now()
                )
                .build();

        return logRepository.save(entry);
    }
}
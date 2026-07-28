package com.example.LoggingService.service;


import com.example.LoggingService.dto.LogMessage;
import com.example.LoggingService.entity.LogEntry;
import com.example.LoggingService.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
//converts the Kafka DTO into a JPA entity and saves it
@Service
public class LoggingService {

    private final LogRepository logRepository;

    public LoggingService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
    @Transactional
    public LogEntry save(LogMessage message) {
        LogEntry entry = LogEntry.builder()
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
package com.example.LoggingService.repository;

import com.example.LoggingService.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository
        extends JpaRepository<LogEntry, UUID> {
}
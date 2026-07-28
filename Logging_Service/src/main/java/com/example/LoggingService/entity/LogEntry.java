package com.example.LoggingService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "message_type", nullable = false, length = 30)
    private String messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "date_time", nullable = false)
    private Instant dateTime;
}
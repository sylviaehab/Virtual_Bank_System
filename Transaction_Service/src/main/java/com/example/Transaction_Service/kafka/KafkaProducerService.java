package com.example.Transaction_Service.kafka;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.Transaction_Service.dto.LogMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducerService {

   private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${transaction.kafka.topic}")
    private String topic;

  public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
}
    public void sendLog(String message, String messageType) {

        LogMessage logMessage = new LogMessage(
                message,
                messageType,
                Instant.now()
        );

        log.info("Sending Kafka message: {}", logMessage);

        kafkaTemplate.send(topic, logMessage)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka message sent successfully");
                    } else {
                        log.error("Kafka send failed", ex);
                    }
                });
    }
}
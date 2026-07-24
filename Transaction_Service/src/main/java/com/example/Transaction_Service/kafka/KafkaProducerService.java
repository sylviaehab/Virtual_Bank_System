package com.example.Transaction_Service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${transaction.kafka.topic}")
    private String topic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLog(String message) {

        log.info("Sending Kafka message to topic {} : {}", topic, message);

        kafkaTemplate.send(topic, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Kafka message sent successfully");
                } else {
                    log.error("Kafka send failed", ex);
                }
            });
    }
}
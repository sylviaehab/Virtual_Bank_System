package com.User_Service.kafka;

import com.User_Service.dto.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
public class LogProducer {

    private static final Logger logger =
            LoggerFactory.getLogger(LogProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public LogProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.log-topic}")
            String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    public void send(
            String messageType,
            String message
    ) {
        try {
            LogMessage logMessage = new LogMessage(
                    message,
                    messageType,
                    Instant.now(),
                    "user-service"
            );

            String payload =
                    objectMapper.writeValueAsString(logMessage);

            kafkaTemplate
                    .send(topicName, payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            logger.error(
                                    "Failed to publish Kafka log",
                                    exception
                            );
                        } else {
                            logger.debug(
                                    "Kafka log published to {}",
                                    topicName
                            );
                        }
                    });

        } catch (Exception exception) {

            logger.error(
                    "Could not serialize Kafka log",
                    exception
            );
        }
    }
}
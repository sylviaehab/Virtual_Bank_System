package com.example.LoggingService.kafka;

import com.example.LoggingService.dto.LogMessage;
import com.example.LoggingService.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class LogConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(LogConsumer.class);

    private final ObjectMapper objectMapper;
    private final LoggingService loggingService;

    public LogConsumer(
            ObjectMapper objectMapper,
            LoggingService loggingService
    ) {
        this.objectMapper = objectMapper;
        this.loggingService = loggingService;
    }

    @KafkaListener(
            topics = "${app.kafka.log-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String payload) {
        try {
            LogMessage message =
                    objectMapper.readValue(
                            payload,
                            LogMessage.class
                    );

            loggingService.save(message);


        } catch (Exception exception) {

            logger.error(
                    "Could not process Kafka log payload: {}",
                    payload,
                    exception
            );
        }
    }
}
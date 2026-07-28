package com.example.LoggingService.kafka;

import com.example.LoggingService.dto.LogMessage;
import com.example.LoggingService.entity.LogEntry;
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

        logger.info(
                "Kafka payload received: {}",
                payload
        );

        LogMessage message;

        /*
         * Only malformed JSON is handled here.
         */
        try {
            message = objectMapper.readValue(
                    payload,
                    LogMessage.class
            );
        } catch (Exception exception) {
            logger.error(
                    "Kafka payload is not valid LogMessage JSON: {}",
                    payload,
                    exception
            );

            /*
             * A malformed message cannot be repaired by retrying.
             */
            return;
        }

        /*
         * Database errors must not be silently swallowed.
         */
        try {
            LogEntry savedEntry =
                    loggingService.save(message);

            logger.info(
                    "Kafka log saved successfully. id={}, type={}",
                    savedEntry.getId(),
                    savedEntry.getMessageType()
            );

        } catch (RuntimeException exception) {
            logger.error(
                    "Kafka message was valid, but saving it to MySQL failed. Payload: {}",
                    payload,
                    exception
            );

            /*
             * Re-throw so Kafka knows message processing failed.
             */
            throw exception;
        }
    }
}
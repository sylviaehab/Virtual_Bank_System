package com.User_Service.kafka;

import com.User_Service.dto.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Component
public class LogProducer {

    private static final Logger logger =
            LoggerFactory.getLogger(LogProducer.class);

    private static final String SERVICE_NAME = "user-service";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;
    private final String topicName;

    public LogProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            JsonMapper jsonMapper,
            @Value("${app.kafka.log-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
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
                    Instant.now()
            );

            String payload =
                    jsonMapper.writeValueAsString(logMessage);

            kafkaTemplate.send(topicName, payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            /*
                             * Kafka logging failed, but the User API
                             * must continue working.
                             */
                            logger.error(
                                    "Failed to publish Kafka log",
                                    exception
                            );
                            return;
                        }

                        logger.debug(
                                "Published Kafka log to topic {}",
                                topicName
                        );
                    });

        } catch (Exception exception) {

            logger.error(
                    "Could not create or publish Kafka log",
                    exception
            );
        }
    }
}
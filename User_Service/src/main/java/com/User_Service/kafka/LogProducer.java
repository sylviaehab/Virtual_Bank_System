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
            Object body
    ) {
        try {
            /*
             * Convert the request or response into inner JSON.
             */
            String innerJson =
                    objectMapper.writeValueAsString(body);

            LogMessage logMessage = new LogMessage(
                    innerJson,
                    messageType,
                    Instant.now()
            );

            /*
             * Convert the complete LogMessage into Kafka JSON.
             */
            String payload =
                    objectMapper.writeValueAsString(logMessage);

            logger.info(
                    "Publishing Kafka log to topic {}: {}",
                    topicName,
                    payload
            );

            kafkaTemplate
                    .send(topicName, payload)
                    .whenComplete((result, exception) -> {

                        if (exception != null) {
                            logger.error(
                                    "Kafka failed to publish log to topic {}",
                                    topicName,
                                    exception
                            );
                            return;
                        }

                        logger.info(
                                "Kafka log published successfully. topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });

        } catch (Exception exception) {
            logger.error(
                    "Could not create Kafka log message",
                    exception
            );
        }
    }
}
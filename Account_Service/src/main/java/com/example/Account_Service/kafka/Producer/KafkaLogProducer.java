package com.example.Account_Service.kafka.Producer;

import com.example.Account_Service.kafka.mapper.KafkaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaMapper kafkaMapper;
    @Value("${account.kafka.topic}")
    private String topic;

    public void sendLog(String message) {

        log.debug("Publishing log message to Kafka topic: {}", topic);

        kafkaTemplate.send(topic, message);

        log.debug("Log message published successfully.");

    }

    public void sendRequest(Object request) {
        try {
            sendLog(
                    kafkaMapper.toRequestLog(request)
            );
        } catch (Exception ex) {
            log.error("Failed to publish Kafka log.", ex);
        }
    }

    public void sendResponse(Object response) {
        try {
            sendLog(
                    kafkaMapper.toResponseLog(response)
            );
        } catch (Exception ex) {
            log.error("Failed to publish Kafka log.", ex);
        }
    }
}

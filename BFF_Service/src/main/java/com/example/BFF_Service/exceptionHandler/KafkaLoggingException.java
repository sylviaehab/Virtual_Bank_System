package com.example.BFF_Service.exceptionHandler;

public class KafkaLoggingException extends RuntimeException {
    public KafkaLoggingException(String message) {
        super(message);
    }
}

package com.example.Account_Service.exceptionHandler;

public class KafkaLoggingException extends RuntimeException {
    public KafkaLoggingException(String message) {
        super(message);
    }
}

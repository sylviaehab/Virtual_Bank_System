package com.example.Account_Service.exceptionHandler;

public class IllegalTransferException extends RuntimeException {
    public IllegalTransferException(String message) {
        super(message);
    }
}

package com.example.Account_Service.ExceptionHandler;

public class IllegalTransferException extends RuntimeException {
    public IllegalTransferException(String message) {
        super(message);
    }
}

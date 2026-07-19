package com.example.User_Service.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {

        super(message);
    }
}

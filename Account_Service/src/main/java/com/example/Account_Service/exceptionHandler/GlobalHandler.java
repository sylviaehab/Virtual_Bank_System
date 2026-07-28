package com.example.Account_Service.exceptionHandler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<GlobalError> handleAccountNotFoundException(AccountNotFoundException ex) {
        return new ResponseEntity<>(new GlobalError(404, "Not Found",
                ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalError> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new GlobalError(404, "Not Found",
                ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CreateException.class)
    public ResponseEntity<GlobalError> handleCreateException(CreateException ex) {
        return new ResponseEntity<>(new GlobalError(400, "Bad Request",
                ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        String message = String.join("\n ", errors);
        return new ResponseEntity<>(new GlobalError(400, "Bad Request",
                message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalTransferException.class)
    public ResponseEntity<GlobalError> handleIllegalTransferException(IllegalTransferException ex) {
        return new ResponseEntity<>(new GlobalError(400, "Bad Request",
                ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<GlobalError> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return new ResponseEntity<>(new GlobalError(400, "Bad Request",
                ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<GlobalError> handleInactiveAccountException(InactiveAccountException ex) {
        return new ResponseEntity<>(new GlobalError(400, "Bad Request",
                ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<GlobalError> handleDownstreamServiceException(DownstreamServiceException ex) {
        return new ResponseEntity<>(new GlobalError(500, "Internal Server Error",
                ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
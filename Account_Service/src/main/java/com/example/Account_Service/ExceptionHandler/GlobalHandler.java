package com.example.Account_Service.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GlobalError> handleNoReNoResourceException(NoResourceFoundException ex) {
        return new ResponseEntity<>(new GlobalError(404, "Not Found",
                ex.getMessage()), HttpStatus.NOT_FOUND);
    }

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

        String message = String.join(", ", errors);
        return new ResponseEntity<>(new GlobalError(400, "Bad Request", message), HttpStatus.BAD_REQUEST);
    }
}
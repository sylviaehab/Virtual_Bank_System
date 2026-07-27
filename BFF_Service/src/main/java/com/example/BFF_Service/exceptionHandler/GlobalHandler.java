package com.example.BFF_Service.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalError> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new GlobalError(404, "Not Found",
                ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<GlobalError> handleDownstreamServiceException(DownstreamServiceException ex) {
        log.error(
                "Downstream service error: {}",
                ex.getMessage()
        );
        return new ResponseEntity<>(new GlobalError(500, "Internal Server Error",
                ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

package com.User_Service.exception;

import com.User_Service.dto.ErrorResponse;
import com.User_Service.kafka.LogProducer;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final LogProducer logProducer;

    public GlobalExceptionHandler(LogProducer logProducer) {
        this.logProducer = logProducer;
    }

    /*
     * Duplicate username or email.
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(
            DuplicateUserException exception
    ) {
        publishErrorLog(
                "User registration failed because the username or email already exists."
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    /*
     * Incorrect username or password.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        /*
         * Do not include a password in this message.
         * Do not reveal whether the username exists.
         */
        publishErrorLog(
                "User login failed because the credentials were invalid."
        );

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }

    /*
     * User profile UUID does not exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception
    ) {
        publishErrorLog(
                "User profile request failed because the user was not found."
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    /*
     * @Valid failed on RegisterRequest or LoginRequest.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String validationMessage =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .collect(Collectors.joining(", "));

        publishErrorLog(
                "User API request failed validation."
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                validationMessage
        );
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        String message =
                "Invalid value for parameter '"
                        + exception.getName()
                        + "'.";

        publishErrorLog(
                "User API request contained an invalid path parameter."
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableJson(
            HttpMessageNotReadableException exception
    ) {
        publishErrorLog(
                "User API request contained malformed JSON."
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "The request body contains invalid JSON."
        );
    }

    /*
     * Final fallback for unexpected server errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception
    ) {
        /*
         * Keep the full technical exception in the User Service's
         * local console logs for developers.
         */
        logger.error(
                "Unexpected User Service error",
                exception
        );

        /*
         * Send only a safe general message to Kafka.
         */
        publishErrorLog(
                "An unexpected error occurred in the User Service."
        );

        /*
         * Do not expose exception.getMessage() to the API client
         * for unknown internal exceptions.
         */
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred."
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message
    ) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private void publishErrorLog(String message) {
        try {
            logProducer.send(
                    "ERROR",
                    message
            );
        } catch (Exception exception) {
            /*
             * This is an additional safeguard.
             * Error handling must still work if logging fails.
             */
            logger.error(
                    "Could not publish error log",
                    exception
            );
        }
    }
}
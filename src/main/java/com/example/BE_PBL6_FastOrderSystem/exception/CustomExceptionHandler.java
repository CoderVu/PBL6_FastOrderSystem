package com.example.BE_PBL6_FastOrderSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling custom authentication exceptions.
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * Handles CustomAuthenticationException and returns a custom response body.
     *
     * @param ex the exception
     * @return a ResponseEntity with the error message and HTTP status
     */
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<String> handleCustomAuthenticationException(CustomAuthenticationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
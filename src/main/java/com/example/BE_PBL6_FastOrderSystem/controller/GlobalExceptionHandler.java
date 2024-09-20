package com.example.BE_PBL6_FastOrderSystem.controller;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.util.Map;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIRespone> handleMissingParams(MissingServletRequestParameterException ex, WebRequest request) {
        String paramName = ex.getParameterName();
        APIRespone apiResponse = APIRespone.builder()
                .status(false)
                .message("Required request parameter '" + paramName + "' is missing")
                .data(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "path", request.getDescription(false)
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<APIRespone> handleAsyncRequestNotUsableException(AsyncRequestNotUsableException ex) {
        APIRespone apiResponse = APIRespone.builder()
                .status(false)
                .message("Request not usable: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "path", "N/A"
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<APIRespone> handleRuntimeException(RuntimeException ex, WebRequest request) {
        APIRespone apiResponse = APIRespone.builder()
                .status(false)
                .message("An error occurred: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "path", request.getDescription(false)
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIRespone> handleGenericException(Exception ex, WebRequest request) {
        APIRespone apiResponse = APIRespone.builder()
                .status(false)
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "path", request.getDescription(false)
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

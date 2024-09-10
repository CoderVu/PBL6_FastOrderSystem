package com.example.BE_PBL6_FastOrderSystem.controller;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

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

}
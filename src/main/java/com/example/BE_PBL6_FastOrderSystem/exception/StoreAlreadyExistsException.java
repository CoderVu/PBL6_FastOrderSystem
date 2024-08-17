package com.example.BE_PBL6_FastOrderSystem.exception;

public class StoreAlreadyExistsException extends RuntimeException {
    public StoreAlreadyExistsException(String message) {
        super(message);
    }
}

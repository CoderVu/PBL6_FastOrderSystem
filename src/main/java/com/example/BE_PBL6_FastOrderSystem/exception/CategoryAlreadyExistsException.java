package com.example.BE_PBL6_FastOrderSystem.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String message)
    {
        super(message);
    }
}

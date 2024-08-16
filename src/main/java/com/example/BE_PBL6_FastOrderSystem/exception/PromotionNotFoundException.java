package com.example.BE_PBL6_FastOrderSystem.exception;

public class PromotionNotFoundException extends RuntimeException {
    public PromotionNotFoundException(Long id) {
        super("Promotion not found with id: " + id);
    }
}

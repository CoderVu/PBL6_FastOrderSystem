package com.example.BE_PBL6_FastOrderSystem.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}

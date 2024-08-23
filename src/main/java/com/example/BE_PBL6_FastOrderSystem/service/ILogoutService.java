package com.example.BE_PBL6_FastOrderSystem.service;

public interface ILogoutService {
    void logout(String token);

    boolean isTokenInvalid(String token);
}

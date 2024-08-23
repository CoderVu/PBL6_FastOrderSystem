package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.service.ILogoutService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class LogoutServiceImpl implements ILogoutService {
    private final Set<String> invalidTokens = new HashSet<>();

    @Override
    public void logout(String token) {
        invalidTokens.add(token);
    }
    @Override
    public boolean isTokenInvalid(String token) {
        return invalidTokens.contains(token);
    }
}


package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

@Data
public class NewTokenResponse {
    private String token;

    public NewTokenResponse(String token) {
        this.token = token;
    }
}

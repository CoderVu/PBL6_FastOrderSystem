package com.example.BE_PBL6_FastOrderSystem.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequest {
    @NotBlank
    private String numberPhone;
    @NotBlank
    private String password;
}
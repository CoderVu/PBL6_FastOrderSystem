package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoMoPaymentRequest {
    private String partnerCode;
    private String accessKey;
    private double amount;
    private Long userId;
    private String orderId;
    private String requestId;
    private String signature;
}
package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long paymentId;
    private Long userId;
    private Long orderId;
    private String paymentMethod;
    private double total;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paymentDate;
    private String OrderCode;
    private String OrderInfo;
}

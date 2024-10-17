package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

@Data
public class PaymentDetailResponse {
    private Long paymentDetailId;
    private Long paymentId;
    private double total;
    private String status;
    private Long orderId;
    private Long storeId;
}

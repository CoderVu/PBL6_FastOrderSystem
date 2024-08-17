package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private Long storeId;
    private Double totalAmount;
    private String status;
    private String paymentMethod;
    private String deliveryAddress;
    private List<OrderDetailRequest> orderDetails;
}
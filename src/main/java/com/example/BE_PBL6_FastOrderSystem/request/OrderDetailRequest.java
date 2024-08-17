package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}
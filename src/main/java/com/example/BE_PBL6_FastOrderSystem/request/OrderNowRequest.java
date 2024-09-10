package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class OrderNowRequest {
    private Long productId;
    private int quantity;
    private String size;
    private Long storeId;
    private String paymentMethod;
    private String deliveryAddress;
    public OrderNowRequest(Long productId, int quantity, String size, Long storeId, String paymentMethod, String deliveryAddress) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.storeId = storeId;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
    }
}

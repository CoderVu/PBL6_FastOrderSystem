package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class CartProductRequest {
    private Long productId;
    private int quantity;
    private String size;
    private Long storeId;
    private String status;
    public CartProductRequest(Long productId, int quantity, String size, Long storeId, String status) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.storeId = storeId;
        this.status = status;
    }
}

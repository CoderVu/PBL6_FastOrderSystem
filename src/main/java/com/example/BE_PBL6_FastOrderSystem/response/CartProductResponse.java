package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CartProductResponse {
    private Long userId;
    private Long productId;
    private String productName;
    private String image;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private Long storeId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public CartProductResponse(Long userId, Long productId, String productName, String image, int quantity, double unitPrice, double totalPrice, Long storeId, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.image = image;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.storeId = storeId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

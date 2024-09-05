package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CartComboResponse {
    private Long userId;
    private Long comboId;
    private String comboName;
    private String image;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private Long storeId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public CartComboResponse(Long userId, Long comboId, String comboName, String image, int quantity, double unitPrice, double totalPrice, Long storeId, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.comboId = comboId;
        this.comboName = comboName;
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

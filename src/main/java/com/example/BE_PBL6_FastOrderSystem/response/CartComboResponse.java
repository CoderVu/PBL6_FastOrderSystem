package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartComboResponse {
    private Long userId;
    private Long comboId;
    private String comboName;
    private String image;
    private List<Long> drinkId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String size;
    private Long storeId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public CartComboResponse(Long userId, Long comboId, String comboName, String image, List<Long> drinkId, int quantity, double unitPrice, double totalPrice, String size, Long storeId, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.comboId = comboId;
        this.comboName = comboName;
        this.image = image;
        this.drinkId = drinkId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.size = size;
        this.storeId = storeId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

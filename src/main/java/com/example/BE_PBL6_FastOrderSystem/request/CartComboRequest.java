package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class CartComboRequest {
    private Long comboId;
    private int quantity;
    private Long storeId;
    private String status;

    public CartComboRequest(Long comboId, int quantity, Long storeId, String status) {
        this.comboId = comboId;
        this.quantity = quantity;
        this.storeId = storeId;
        this.status = status;
    }
}

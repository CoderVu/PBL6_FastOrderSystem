package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

import java.util.List;

@Data
public class CartComboRequest {
    private Long comboId;
    private List<Long> drinkId;
    private int quantity;
    private String size;
    private Long storeId;
    private String status;

    public CartComboRequest(Long comboId, List<Long> drinkId, int quantity, String size, Long storeId, String status) {
        this.comboId = comboId;
        this.drinkId = drinkId;
        this.quantity = quantity;
        this.size = size;
        this.storeId = storeId;
        this.status = status;
    }
}

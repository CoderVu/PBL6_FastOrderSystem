package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class UpdateQuantityRequest {
    private Long productId = null;
    private Long comboId = null;
    private Long storeId;
    private int quantity;

    public UpdateQuantityRequest(Long productId, Long comboId, Long storeId, int quantity) {
        this.productId = productId;
        this.comboId = comboId;
        this.storeId = storeId;
        this.quantity = quantity;
    }


}

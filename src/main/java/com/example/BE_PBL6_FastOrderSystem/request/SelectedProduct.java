package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class SelectedProduct {
    private Long productId;
    private String groupName;
    public SelectedProduct(Long productId, String groupName) {
        this.productId = productId;
        this.groupName = groupName;
    }
}

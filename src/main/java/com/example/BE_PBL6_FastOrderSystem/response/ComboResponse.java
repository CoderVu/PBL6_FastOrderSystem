package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.util.List;

@Data
public class ComboResponse {
    private Long comboId;
    private String comboName;
    private double price;
    private String image;
    private String description;
    private List<ProductResponse> products;

    public ComboResponse(Long comboId, String comboName, double price, String image, String description, List<ProductResponse> products) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.price = price;
        this.image = image;
        this.description = description;
        this.products = products;
    }
}
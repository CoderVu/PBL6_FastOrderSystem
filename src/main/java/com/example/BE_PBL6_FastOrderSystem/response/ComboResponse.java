package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboResponse {
    private Long comboId;
    private String comboName;
    private double price;
    private double averageRate;
    private String image;
    private String description;
    private List<ProductResponse> products;
}
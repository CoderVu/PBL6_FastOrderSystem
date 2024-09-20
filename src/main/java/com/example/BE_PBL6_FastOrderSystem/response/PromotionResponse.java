package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class PromotionResponse {
    private Long id;
    private String name;
    private String description;
    private double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> storeIds;


    public PromotionResponse(Long id, String name, String description, double discountPercentage, LocalDateTime startDate, LocalDateTime endDate, List<Long> storeIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storeIds = storeIds;
    }

}
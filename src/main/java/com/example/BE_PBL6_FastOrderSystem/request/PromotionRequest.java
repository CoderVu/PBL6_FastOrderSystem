package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
@Data
public class PromotionRequest {
    private String name;
    private String description;
    private MultipartFile image;
    private Double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    public PromotionRequest(String name, String description, MultipartFile image,  Double discountPercentage, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}

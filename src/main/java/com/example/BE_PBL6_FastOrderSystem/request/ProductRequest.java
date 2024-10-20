package com.example.BE_PBL6_FastOrderSystem.request;

import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {
    private String productName;
    private MultipartFile image;
    private String description;
    private Double price;
    private Long categoryId;
    private Integer stockQuantity;
    private Boolean bestSale;
    public ProductRequest(String productName, MultipartFile image, String description, Double price, Long categoryId, Integer stockQuantity, Boolean bestSale) {
        this.productName = productName;
        this.image = image;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.bestSale = bestSale;
    }
    public String getProductName() {
        return productName;
    }

    public MultipartFile getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Long getCategoryId() {
        return categoryId;
    }


    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Boolean getBestSale() {
        return bestSale;
    }
}
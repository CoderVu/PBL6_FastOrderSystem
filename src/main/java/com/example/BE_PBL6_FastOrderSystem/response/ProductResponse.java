package com.example.BE_PBL6_FastOrderSystem.response;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResponse {
    private Long productId;
    private String productName;
    private String image;
    private String description;
    private Double price;
    private Double discountedPrice;  // Discounted price
    private CategoryResponse category;
    private List<StoreResponse> stores;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean bestSale;

    public ProductResponse(Long productId, String productName, String image, String description, Double price, Double discountedPrice, CategoryResponse category, List<StoreResponse> stores, Integer stockQuantity, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean bestSale) {
        this.productId = productId;
        this.productName = productName;
        this.image = image;
        this.description = description;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.category = category;
        this.stores = stores;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bestSale = bestSale;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public List<StoreResponse> getStores() {
        return stores;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getBestSale() {
        return bestSale;
    }

    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
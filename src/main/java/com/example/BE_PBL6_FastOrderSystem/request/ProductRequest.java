package com.example.BE_PBL6_FastOrderSystem.request;

public class ProductRequest {
    private String productName;
    private String image;
    private String description;
    private Double price;
    private Long categoryId;
    private Long storeId;
    private Integer stockQuantity;
    private Boolean bestSale;

    public ProductRequest(String productName, String image, String description, Double price, Long categoryId, Long storeId, Integer stockQuantity, Boolean bestSale) {
        this.productName = productName;
        this.image = image;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.storeId = storeId;
        this.stockQuantity = stockQuantity;
        this.bestSale = bestSale;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Boolean getBestSale() {
        return bestSale;
    }
}
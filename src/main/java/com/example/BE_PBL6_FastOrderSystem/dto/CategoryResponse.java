package com.example.BE_PBL6_FastOrderSystem.dto;

public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private String description;

    public CategoryResponse(Long categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }
}
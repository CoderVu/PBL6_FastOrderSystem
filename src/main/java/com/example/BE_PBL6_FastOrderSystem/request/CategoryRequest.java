package com.example.BE_PBL6_FastOrderSystem.request;

public class CategoryRequest {
    private String categoryName;
    private String description;

    public CategoryRequest(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }
}

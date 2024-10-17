package com.example.BE_PBL6_FastOrderSystem.request;

import org.springframework.web.multipart.MultipartFile;

public class CategoryRequest {
    private String categoryName;
    private MultipartFile image;
    private String description;

    public CategoryRequest(String categoryName, MultipartFile image, String description) {
        this.categoryName = categoryName;
        this.image = image;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public MultipartFile getImage() {
        return image;
    }
    public String getDescription() {
        return description;
    }
}

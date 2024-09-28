package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Getter;
@Getter
public class CategoryResponse {
    private final Long categoryId;
    private final String categoryName;
    private final String description;
    private final String image;

    public CategoryResponse(Long categoryId, String categoryName, String image ,String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.image = image;
        this.description = description;
    }

}
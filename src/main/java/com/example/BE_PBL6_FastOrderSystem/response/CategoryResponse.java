package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Getter;

import java.util.Objects;

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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryResponse that = (CategoryResponse) o;
        return Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash( categoryName);
    }
}
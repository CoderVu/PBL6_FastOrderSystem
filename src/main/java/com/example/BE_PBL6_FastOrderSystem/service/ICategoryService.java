package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.request.CategoryRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICategoryService {
    ResponseEntity<APIRespone> getAllCategories();
    ResponseEntity<APIRespone>  getCategoryById(Long categoryId);
    ResponseEntity<APIRespone>  addCategory(CategoryRequest categoryRequest);
    ResponseEntity<APIRespone>  updateCategory(Long categoryId, CategoryRequest categoryRequest);
    ResponseEntity<APIRespone>  deleteCategory(Long categoryId);
    ResponseEntity<APIRespone> getCategoryByStoreId(Long storeId);
}

package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.request.CategoryRequest;
import com.example.BE_PBL6_FastOrderSystem.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final ICategoryService categoryService;
    @PostMapping("/add")
    public ResponseEntity<APIRespone> addCategory(@RequestParam String categoryName, @RequestParam MultipartFile image, @RequestParam String description) {
        CategoryRequest categoryRequest = new CategoryRequest(categoryName, image, description);
        return categoryService.addCategory(categoryRequest);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<APIRespone> updateCategory(@PathVariable Long id, @RequestParam String categoryName, @RequestParam MultipartFile image, @RequestParam String description) {
        CategoryRequest categoryRequest = new CategoryRequest(categoryName, image, description);
      return categoryService.updateCategory(id, categoryRequest);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<APIRespone>  deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
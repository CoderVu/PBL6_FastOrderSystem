package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.request.CategoryRequest;
import com.example.BE_PBL6_FastOrderSystem.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final ICategoryService categoryService;
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest) {
        try {
            CategoryResponse categoryResponse = categoryService.addCategory(categoryRequest);
            return ResponseEntity.ok(categoryResponse);
        } catch (AlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
       try {
           CategoryResponse categoryResponse = categoryService.updateCategory(id, categoryRequest);
           return ResponseEntity.ok(categoryResponse);
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
       }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().body("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
    }
}
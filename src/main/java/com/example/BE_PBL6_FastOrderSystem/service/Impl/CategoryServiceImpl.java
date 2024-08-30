package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Category;
import com.example.BE_PBL6_FastOrderSystem.repository.CategoryRepository;
import com.example.BE_PBL6_FastOrderSystem.request.CategoryRequest;
import com.example.BE_PBL6_FastOrderSystem.service.ICategoryService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl  implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<APIRespone> getAllCategories() {
      if (categoryRepository.findAll().isEmpty()) {
          return ResponseEntity.badRequest().body(new APIRespone(false, "No categories found", ""));
      }
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", categories));
    }

    @Override
    public  ResponseEntity<APIRespone> getCategoryById(Long categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Category not found", ""));
        }
        Category category = categoryRepository.findById(categoryId).get();
        return ResponseEntity.ok(new APIRespone(true, "Success", new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getDescription())));
    }


    @Override
    public ResponseEntity<APIRespone>  addCategory(CategoryRequest categoryRequest) {
         if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
             return ResponseEntity.badRequest().body(new APIRespone(false, "Category already exists", ""));
         }
        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        try {
            InputStream inputStream = categoryRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(inputStream);
            category.setImage(base64Image);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Error when upload image", ""));
        }
        category.setDescription(categoryRequest.getDescription());
        category = categoryRepository.save(category);
        return ResponseEntity.ok(new APIRespone(true, "Add category successfully", new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getDescription())));
    }

    @Override
    public ResponseEntity<APIRespone> updateCategory(Long id, CategoryRequest categoryRequest) {
         if (categoryRepository.findById(id).isEmpty()) {
             return ResponseEntity.badRequest().body(new APIRespone(false, "Category not found", ""));
         }
         if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
             return ResponseEntity.badRequest().body(new APIRespone(false, "Category already exists", ""));
         }
        Category category = categoryRepository.findById(id).get();
        category.setCategoryName(categoryRequest.getCategoryName());
        try {
            InputStream inputStream = categoryRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(inputStream);
            category.setImage(base64Image);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Error when upload image", ""));
        }
        category.setDescription(categoryRequest.getDescription());
        category = categoryRepository.save(category);
        return ResponseEntity.ok(new APIRespone(true, "Update category successfully", new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getDescription())));
    }

    @Override
    public ResponseEntity<APIRespone>  deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Category not found", ""));
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(new APIRespone(true, "Delete category successfully", ""));
    }
}

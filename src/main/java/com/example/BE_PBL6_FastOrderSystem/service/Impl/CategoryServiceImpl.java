package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.request.CategoryRequest;
import com.example.BE_PBL6_FastOrderSystem.service.ICategoryService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl  implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartRepository;
    private final PromotionRepository promotionRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public ResponseEntity<APIRespone> getAllCategories() {
      if (categoryRepository.findAll().isEmpty()) {
          return ResponseEntity.badRequest().body(new APIRespone(false, "No categories found", ""));
      }
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getImage() ,category.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", categories));
    }

    @Override
    public  ResponseEntity<APIRespone> getCategoryById(Long categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Category not found", ""));
        }
        Category category = categoryRepository.findById(categoryId).get();
        return ResponseEntity.ok(new APIRespone(true, "Success", new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getImage(), category.getDescription())));
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
        return ResponseEntity.ok(new APIRespone(true, "Add category successfully", new CategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getImage(), category.getDescription())));
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
        return ResponseEntity.ok(new APIRespone(true, "Update category successfully", new CategoryResponse(category.getCategoryId(), category.getCategoryName(),category.getImage(),  category.getDescription())));
    }
    @Override
    public ResponseEntity<APIRespone> deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Category not found", ""));
        }
        List<Product> products = category.get().getProducts();
        for (Product product : products) {
            productRepository.delete(product);
            Set<Combo> combos = product.getCombos();
            for (Combo combo : combos) {
                combo.getProducts().remove(product);
            }
        }
        List<Cart> carts = cartRepository.findByProductIn(products);
        for (Cart cart : carts) {
            cartRepository.delete(cart);
        }
        List<Promotion> promotions = promotionRepository.findPromotionsByProductsIn(products);
        for (Promotion promotion : promotions) {
            promotion.getProducts().removeAll(products);
        }
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailsByProductIn(products);
        for (OrderDetail orderDetail : orderDetails) {
            orderDetailRepository.delete(orderDetail);
        }

        categoryRepository.deleteById(id);

        return ResponseEntity.ok(new APIRespone(true, "Delete category successfully", ""));
    }

}

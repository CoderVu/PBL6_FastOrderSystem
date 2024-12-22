package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.request.CategoryRequest;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ResponseConverter;
import com.example.BE_PBL6_FastOrderSystem.service.ICategoryService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import com.example.BE_PBL6_FastOrderSystem.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
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
    private final StoreRepository storeRepository;

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
    public ResponseEntity<APIRespone> getCategoryByStoreId(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        if (store.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }
        Set<CategoryResponse> set = new HashSet<>();
        List<ProductResponse> productResponses = productRepository.findByStoreId(storeId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .toList();
        for (ProductResponse item : productResponses) {
            set.add(item.getCategory());
        }
        return new ResponseEntity<>(new APIRespone(true, "Success", set), HttpStatus.OK);
    }
@Override
public ResponseEntity<APIRespone> addCategory(CategoryRequest categoryRequest) {
    if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
        return ResponseEntity.badRequest().body(new APIRespone(false, "Category already exists", ""));
    }
    Category category = new Category();
    category.setCategoryName(categoryRequest.getCategoryName());
    if (categoryRequest.getImage() != null) {
        try {
            String normalizedProductName = StringUtils.normalizeString(categoryRequest.getCategoryName());
            String imageName = normalizedProductName + "_" + System.currentTimeMillis() + ".jpg";
            Path imagePath = Paths.get("uploads/images/" + imageName);
            Files.createDirectories(imagePath.getParent());
            Files.copy(categoryRequest.getImage().getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            category.setImage(imageName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Error when uploading image", ""));
        }
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
        Category category = categoryRepository.findById(id).get();
        category.setCategoryName(categoryRequest.getCategoryName());
        try {
            if (categoryRequest.getImage() != null) {
               Path oldImagePath = Paths.get("uploads/images/" + category.getImage());
                Files.deleteIfExists(oldImagePath);
            }
            String normalizedProductName = StringUtils.normalizeString(categoryRequest.getCategoryName());
            String imageName = normalizedProductName + "_" + System.currentTimeMillis() + ".jpg";
            Path imagePath = Paths.get("uploads/images/" + imageName);
            Files.createDirectories(imagePath.getParent());
            Files.copy(categoryRequest.getImage().getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            category.setImage(imageName);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Error when uploading image", ""));
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
            List<Combo> combos = product.getCombos();
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
        try {
            Path imagePath = Paths.get("uploads/images/" + category.get().getImage());
            Files.deleteIfExists(imagePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Error when deleting image", ""));
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(new APIRespone(true, "Delete category successfully", ""));
    }
}

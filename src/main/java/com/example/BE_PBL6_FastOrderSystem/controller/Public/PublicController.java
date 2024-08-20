package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.response.*;
import com.example.BE_PBL6_FastOrderSystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/public")
public class PublicController {
    private final IProductService productService;
    private final IComboService comboService;
    private final ICategoryService categoryService;
    private final IPromotionService promotionService;
    private final IStoreService storeService;
    @GetMapping("/categories/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryResponses);
    }
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryResponse);
    }
    @GetMapping("/products/all")
    public ResponseEntity<APIRespone> getAllProducts() throws SQLException {
       return productService.getAllProduct();

    }
    @GetMapping("/products/{id}")
    public ResponseEntity<APIRespone> getProductById(@PathVariable("id") Long productId) {
        return productService.getProductById(productId);
    }
    @GetMapping("/products/store/{storeId}")
    public ResponseEntity<APIRespone> getProductsByStoreId(@PathVariable("storeId") Long storeId) {
       return productService.getProductsByStoreId(storeId);
    }
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<APIRespone> getProductsByCategoryId(@PathVariable("categoryId") Long categoryId) {
       return productService.getProductsByCategoryId(categoryId);
    }
    @GetMapping("/products/search")
    public ResponseEntity<APIRespone> getProductByNames(@RequestParam("name") String productName) {
     return productService.getProductByNames(productName);
    }
    @GetMapping("/products/best-sale")
    public ResponseEntity<APIRespone> getBestSaleProducts() {
       return productService.getBestSaleProduct();
    }
    @GetMapping("/products/combos")
    public ResponseEntity<?> getCombos() {
        List<ComboResponse> products = comboService.getAllCombos();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }
    @GetMapping("/products/combos/{id}")
    public ResponseEntity<?> getProductsByComboId(@PathVariable("id") Long comboId) {
        List<ProductResponse> products = comboService.getProductsByComboId(comboId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }
    @GetMapping("/stores/all")
    public ResponseEntity<List<StoreResponse>> getStores() {
        List<StoreResponse> storeResponses = storeService.getAllStores();
        return ResponseEntity.ok(storeResponses);
    }

    @GetMapping("/promotions/all")
    public ResponseEntity <List<PromotionResponse>> getPromotions() {
        List<PromotionResponse> promotionResponses = promotionService.getAllPromotion();
        return ResponseEntity.ok(promotionResponses);

    }
    @GetMapping("/promotions/{id}")
    public ResponseEntity<?> getPromotionById(@PathVariable("id") Long promotionId) {
        Optional<PromotionResponse> promotionResponse = promotionService.getPromotionById(promotionId);
        if (promotionResponse.isPresent()) {
            return ResponseEntity.ok(promotionResponse.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

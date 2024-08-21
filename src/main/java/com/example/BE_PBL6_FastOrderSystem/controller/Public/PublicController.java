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
    public  ResponseEntity<APIRespone> getAllCategories() {
     return categoryService.getAllCategories();
    }
    @GetMapping("/categories/{id}")
    public ResponseEntity<APIRespone>  getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
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
       return comboService.getAllCombos();
    }
    @GetMapping("/products/combos/{id}")
    public ResponseEntity<?> getProductsByComboId(@PathVariable("id") Long comboId) {
       return comboService.getProductsByComboId(comboId);
    }
    @GetMapping("/stores/all")
    public ResponseEntity<APIRespone> getStores() {
         return storeService.getAllStores();
    }
    @GetMapping("/stores/{id}")
    public ResponseEntity<APIRespone> getStoreById(@PathVariable("id") Long storeId) {
        return storeService.getStoreById(storeId);
    }
    @GetMapping("/promotions/all")
    public ResponseEntity<APIRespone> getPromotions() {
       return promotionService.getAllPromotion();

    }
    @GetMapping("/promotions/{id}")
    public ResponseEntity<?> getPromotionById(@PathVariable("id") Long promotionId) {
         return promotionService.getPromotionById(promotionId);
    }

}

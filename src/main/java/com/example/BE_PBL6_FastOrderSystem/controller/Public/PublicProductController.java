package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.service.ICategoryService;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
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
public class PublicProductController {
    private final IProductService productService;
    private final IComboService comboService;
    private final ICategoryService categoryService;
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
    public ResponseEntity<List<ProductResponse>> getAllProducts() throws SQLException {
        List<ProductResponse> products = productService.getAllProduct();
        return ResponseEntity.ok(products);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        Optional<ProductResponse> productResponse = productService.getProductById(productId);
        if (productResponse.isPresent()) {
            return ResponseEntity.ok(productResponse.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/products/store/{storeId}")
    public ResponseEntity<?> getProductsByStoreId(@PathVariable("storeId") Long storeId) {
        List<ProductResponse> products = productService.getProductsByStoreId(storeId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryId(@PathVariable("categoryId") Long categoryId) {
        List<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }
    @GetMapping("/products/search")
    public ResponseEntity<?> getProductByNames(@RequestParam("name") String productName) {
        List<ProductResponse> products = productService.getProductByNames(productName);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }
    @GetMapping("/products/best-sale")
    public ResponseEntity<?> getBestsellingCombos() {
        List<ProductResponse> products = productService.getBestSaleProduct();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
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

}

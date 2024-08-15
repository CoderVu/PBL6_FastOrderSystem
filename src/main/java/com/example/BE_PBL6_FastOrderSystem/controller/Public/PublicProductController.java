package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.dto.ProductResponse;
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
@RequestMapping("/api/v1/public/products")
public class PublicProductController {
    private final IProductService productService;
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() throws SQLException {
        List<ProductResponse> products = productService.getAllProduct();
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        Optional<ProductResponse> productResponse = productService.getProductById(productId);
        if (productResponse.isPresent()) {
            return ResponseEntity.ok(productResponse.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getProductsByStoreId(@PathVariable("storeId") Long storeId) {
        List<ProductResponse> products = productService.getProductsByStoreId(storeId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }

}

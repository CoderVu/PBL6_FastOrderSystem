package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.ProductAlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final IProductService productService;
    private final IStoreService storeService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("storeId") Long storeId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("bestSale") Boolean bestSale) throws IOException {
        try {
            ProductRequest productRequest = new ProductRequest(productName, image, description, price, categoryId, storeId, stockQuantity, bestSale);
            productService.addProduct(productRequest);
            return ResponseEntity.ok().body("Product added successfully");
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("storeId") Long storeId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("bestSale") Boolean bestSale) {
        try {
            ProductRequest productRequest = new ProductRequest(productName, image, description, price, categoryId, storeId, stockQuantity, bestSale);
            productService.updateProduct(id, productRequest);
            return ResponseEntity.ok().body("Product updated successfully");
        }
        catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body("Product deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
}
package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final IProductService productService;
    private final IStoreService storeService;

    @PostMapping("/add")
    public ResponseEntity<APIRespone> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("storeId") Long storeId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("bestSale") Boolean bestSale)  {
        ProductRequest productRequest = new ProductRequest(productName, image, description, price, categoryId, storeId, stockQuantity, bestSale);
        return productService.addProduct(productRequest);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<APIRespone> updateProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("storeId") Long storeId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("bestSale") Boolean bestSale) {
        ProductRequest productRequest = new ProductRequest(productName, image, description, price, categoryId, storeId, stockQuantity, bestSale);
        return productService.updateProduct(id, productRequest);

    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<APIRespone>  deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
}
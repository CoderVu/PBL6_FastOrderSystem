package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            @RequestParam("image") MultipartFile image,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("bestSale") Boolean bestSale)  {
        ProductRequest productRequest = new ProductRequest(productName, image, description, price, categoryId, stockQuantity, bestSale);
        return productService.addProduct(productRequest);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<APIRespone> updateProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("bestSale") Boolean bestSale) {
        ProductRequest productRequest = new ProductRequest(productName, image, description, price, categoryId, stockQuantity, bestSale);
        return productService.updateProduct(id, productRequest);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<APIRespone>  deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
    @PostMapping("/apply-to-all-stores")
    public ResponseEntity<APIRespone> applyProductToAllStores(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        return productService.applyProductToAllStores(productId, quantity);
    }
    @PostMapping("/apply-list-products-to-store")
    public ResponseEntity<APIRespone> applyListProductsToStore(
            @RequestParam("storeId") Long storeId,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("quantity") Integer quantity) {
        return productService.applyProductsToStore(productIds, storeId, quantity);
    }
    @PostMapping("/apply-all-products-to-store")
    public ResponseEntity<APIRespone> applyAllProductsToStore(
            @RequestParam("storeId") Long storeId,
            @RequestParam("quantity") Integer quantity) {
        return productService.applyAllProductsToStore(storeId, quantity);
    }
    @PostMapping("/apply-all-products-to-all-stores")
    public ResponseEntity<APIRespone> applyAllProductsToAllStores(
            @RequestParam("quantity") Integer quantity) {
        return productService.applyAllProductsToAllStores(quantity);
    }

    @PostMapping("/remove-from-store")
    public ResponseEntity<APIRespone> removeProductFromStore(
            @RequestParam("storeId") Long storeId,
            @RequestParam("productId") Long productId) {
        return productService.removeProductFromStore(storeId, productId);
    }
    @PostMapping("/remove-list-products-from-store")
    public ResponseEntity<APIRespone> removeListProductsFromStore(
            @RequestParam("storeId") Long storeId,
            @RequestParam("productIds") List<Long> productIds) {
        return productService.removeProductsFromStore(productIds, storeId);
    }
}
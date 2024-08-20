package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    ResponseEntity<APIRespone> getAllProduct();
    ResponseEntity<APIRespone> getProductById(Long productId);
    ResponseEntity<APIRespone> getProductsByStoreId(Long storeId);
    ResponseEntity<APIRespone> getProductsByCategoryId(Long categoryId);
    ResponseEntity<APIRespone> getProductByNames(String productName);
    ResponseEntity<APIRespone> getBestSaleProduct();
    ResponseEntity<APIRespone> addProduct(ProductRequest productRequest);
    ResponseEntity<APIRespone> updateProduct(Long id, ProductRequest productRequest);
    ResponseEntity<APIRespone> deleteProduct(Long id);
}


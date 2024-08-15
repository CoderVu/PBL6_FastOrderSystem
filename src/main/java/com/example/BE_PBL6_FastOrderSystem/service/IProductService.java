package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<ProductResponse> getAllProduct();
    Optional<ProductResponse> getProductById(Long productId);
    List<ProductResponse> getProductsByStoreId(Long storeId);
    List<ProductResponse> getProductsByCategoryId(Long categoryId);
    List<ProductResponse> getProductByNames(String productName);
    List<ProductResponse> getBestSaleProduct();
}


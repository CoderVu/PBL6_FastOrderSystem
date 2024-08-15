package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.dto.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<ProductResponse> getAllProduct();
    Optional<ProductResponse> getProductById(Long productId);
    List<ProductResponse> getProductsByStoreId(Long storeId);
}


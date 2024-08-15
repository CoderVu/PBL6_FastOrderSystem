package com.example.BE_PBL6_FastOrderSystem.util;

import com.example.BE_PBL6_FastOrderSystem.dto.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.dto.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.dto.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Product;

public class ResponseConverter {
    public static ProductResponse convertToProductResponse(Product product) {
        CategoryResponse categoryResponse  = new CategoryResponse(
                product.getCategory().getCategoryId(),
                product.getCategory().getCategoryName(),
                product.getCategory().getDescription()
        );
        StoreResponse storeResponse = new StoreResponse(
                product.getStore().getStoreId(),
                product.getStore().getStoreName(),
                product.getStore().getLocation(),
                product.getStore().getCreatedAt(),
                product.getStore().getUpdatedAt()
        );

        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                categoryResponse,
                storeResponse,
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getBestSale()
        );
    }
}
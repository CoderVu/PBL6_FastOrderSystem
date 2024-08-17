package com.example.BE_PBL6_FastOrderSystem.util;

import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.response.*;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Promotion;

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
                product.getStore().getLongitude(),
                product.getStore().getLatitude(),
                product.getStore().getPhoneNumber(),
                product.getStore().getOpeningTime(),
                product.getStore().getClosingTime(),
                product.getStore().getCreatedAt(),
                product.getStore().getUpdatedAt()
        );

        // Calculate discounted price if a promotion exists
        Double discountedPrice = product.getPrice();
        if (product.getPromotion() != null) {
            Promotion promotion = product.getPromotion();
            double discountPercentage = promotion.getDiscountPercentage();
            discountedPrice = product.getPrice() * (1 - discountPercentage / 100);
        }

        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                discountedPrice,
                categoryResponse,
                storeResponse,
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getBestSale()
        );
    }

}

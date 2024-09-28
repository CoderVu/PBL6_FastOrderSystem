package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Promotion;
import com.example.BE_PBL6_FastOrderSystem.model.Store;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseConverter {
    public static ProductResponse convertToProductResponse(Product product) {
        CategoryResponse categoryResponse = new CategoryResponse(
                product.getCategory().getCategoryId(),
                product.getCategory().getCategoryName(),
                product.getCategory().getImage(),
                product.getCategory().getDescription()
        );
        List<StoreResponse> storeResponses = product.getProductStores().stream()
                .map(productStore -> {
                    Store store = productStore.getStore();
                    return new StoreResponse(
                            store.getStoreId(),
                            store.getStoreName(),
                            store.getImage(),
                            store.getLocation(),
                            store.getLongitude(),
                            store.getLatitude(),
                            store.getPhoneNumber(),
                            store.getOpeningTime(),
                            store.getClosingTime(),
                            store.getCreatedAt(),
                            store.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
        // Calculate discounted price if a promotion exists
        Double discountedPrice = product.getPrice();
        if (product.getPromotions() != null && !product.getPromotions().isEmpty()) {
            double maxDiscountPercentage = product.getPromotions().stream()
                    .mapToDouble(Promotion::getDiscountPercentage)
                    .max()
                    .orElse(0);
            discountedPrice = product.getPrice() * (1 - maxDiscountPercentage / 100);
        }

        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                discountedPrice,
                categoryResponse,
                storeResponses,
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getBestSale()
        );
    }
    public static ComboResponse convertToComboResponse(Combo combo) {
        List<ProductResponse> productResponses = combo.getProducts().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());

        return new ComboResponse(
                combo.getComboId(),
                combo.getComboName(),
                combo.getComboPrice(),
                combo.getImage(),
                combo.getDescription(),
                productResponses
        );
    }
}
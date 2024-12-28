package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.entity.*;

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
                            store.getUpdatedAt(),
                            productStore.getStockQuantity()
                    );
                })
                .collect(Collectors.toList());
        Double discountedPrice = product.getDiscountedPrice();
        if (discountedPrice == null || discountedPrice == 0.0) {
            discountedPrice = product.getPrice();
        }
        // Calculate average rate if rates = null thi average rate = 0
        double averageRate = 0.0;
        if (product.getRates() != null && !product.getRates().isEmpty()) {
            averageRate = product.getRates().stream()
                    .mapToDouble(Rate::getRate)
                    .average()
                    .orElse(0);
        }

        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                discountedPrice,
                averageRate,
                categoryResponse,
                storeResponses,
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getBestSale()
        );
    }
    public static ProductResponse convertToProductOfStoreResponse(ProductStore productStore) {
        Product product = productStore.getProduct();
        CategoryResponse categoryResponse = new CategoryResponse(
                product.getCategory().getCategoryId(),
                product.getCategory().getCategoryName(),
                product.getCategory().getImage(),
                product.getCategory().getDescription()
        );
        Store store = productStore.getStore();
        StoreResponse storeResponse = new StoreResponse(
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
                store.getUpdatedAt(),
                productStore.getStockQuantity()
        );
        Double discountedPrice = product.getDiscountedPrice();
        if (discountedPrice == null || discountedPrice == 0.0) {
            discountedPrice = product.getPrice();
        }
        // Calculate average rate if rates = null thi average rate = 0
        double averageRate = 0.0;
        if (product.getRates() != null && !product.getRates().isEmpty()) {
            averageRate = product.getRates().stream()
                    .mapToDouble(Rate::getRate)
                    .average()
                    .orElse(0);
        }
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                discountedPrice,
                averageRate,
                categoryResponse,
                List.of(storeResponse),
                productStore.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getBestSale()
        );
    }
    public static ComboResponse convertToComboResponse(Combo combo) {
        List<ProductResponse> productResponses = combo.getProducts().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        double averageRate = 0.0;
        if (combo.getRates() != null && !combo.getRates().isEmpty()) {
            averageRate = combo.getRates().stream()
                    .mapToDouble(Rate::getRate)
                    .average()
                    .orElse(0);
        }
        return new ComboResponse(
                combo.getComboId(),
                combo.getComboName(),
                combo.getComboPrice(),
                averageRate,
                combo.getImage(),
                combo.getDescription(),
                combo.getNumberDrinks(),
                productResponses
        );
    }
}
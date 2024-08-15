package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.dto.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.dto.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.dto.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.repo.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> getAllProduct() {
        return productRepository.findAll().stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(this::convertToProductResponse);
    }
    @Override
    public List<ProductResponse> getProductsByStoreId(Long storeId) {
        return productRepository.findByStore_StoreId(storeId).stream()// Find by storeId
                .map(this::convertToProductResponse)// Convert to ProductResponse
                .collect(Collectors.toList()); // Convert to List
    }

    private ProductResponse convertToProductResponse(Product product) {
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
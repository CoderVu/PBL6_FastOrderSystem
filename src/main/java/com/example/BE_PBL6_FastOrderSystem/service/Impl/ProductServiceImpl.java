package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.ProductAlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.Category;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.repository.CategoryRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.util.ResponseConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Override
    public List<ProductResponse> getAllProduct() {
        return productRepository.findAll().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(ResponseConverter::convertToProductResponse);
    }

    @Override
    public List<ProductResponse> getProductsByStoreId(Long storeId) {
        return productRepository.findByStore_StoreId(storeId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductByNames(String productName) {
        return productRepository.findByProductNameContaining(productName).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getBestSaleProduct() {
        return productRepository.findByBestSale(true).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        if (productRepository.existsByProductName(productRequest.getProductName())) {
            throw new ProductAlreadyExistsException("Product already exists");
        }
       if (!categoryRepository.existsById(productRequest.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found");
        }
        if (!storeRepository.existsById(productRequest.getStoreId())) {
            throw new ResourceNotFoundException("Store not found");
        }
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setImage(productRequest.getImage());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        Category category = categoryRepository.findById(productRequest.getCategoryId()).get();
        product.setCategory(category);
        Store store = storeRepository.findById(productRequest.getStoreId()).get();
        product.setStore(store);
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setBestSale(productRequest.getBestSale());
        product = productRepository.save(product);
        return ResponseConverter.convertToProductResponse(product);
    }
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setProductName(productRequest.getProductName());
        product.setImage(productRequest.getImage());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        Category category = categoryRepository.findById(productRequest.getCategoryId()).get();
        product.setCategory(category);
        Store store = storeRepository.findById(productRequest.getStoreId()).get();
        product.setStore(store);
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setBestSale(productRequest.getBestSale());
        product = productRepository.save(product);
        return ResponseConverter.convertToProductResponse(product);
    }
@Override
public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }


}
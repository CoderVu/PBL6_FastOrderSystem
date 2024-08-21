package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Category;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.repository.CategoryRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import com.example.BE_PBL6_FastOrderSystem.utils.ResponseConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Override
    public ResponseEntity<APIRespone>  getAllProduct() {
       if (productRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "No product found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findAll().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone>  getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
        }
        ProductResponse productResponse = ResponseConverter.convertToProductResponse(product.get());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponse), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> getProductsByStoreId(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        if (store.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findByStores_StoreId(storeId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> getProductsByCategoryId(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Category not found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findByCategory_CategoryId(categoryId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> getProductByNames(String productName) {
        if (productRepository.findByProductNameContaining(productName).isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findByProductNameContaining(productName).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }

    @Override
    public  ResponseEntity<APIRespone> getBestSaleProduct() {
        if (productRepository.findByBestSale(true).isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "No best sale product found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findByBestSale(true).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }


    @Override
    public  ResponseEntity<APIRespone> addProduct(ProductRequest productRequest) {
        if (productRepository.existsByProductName(productRequest.getProductName())) {
            return new ResponseEntity<>(new APIRespone(false, "Product already exists", ""), HttpStatus.BAD_REQUEST);
        }
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        try {
            InputStream imageInputStream = productRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(imageInputStream);
            product.setImage(base64Image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId()); // Optional laf 1 kieu du lieu giup kiem tra xem co ton tai hay khong
        if (category.isEmpty()) {
           return new ResponseEntity<>(new APIRespone(false, "Category not found", ""), HttpStatus.NOT_FOUND);
        }
        product.setCategory(category.get());
        Optional<Store> store = storeRepository.findById(productRequest.getStoreId());
        if (store.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }
        product.getStores().add(store.get());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setBestSale(productRequest.getBestSale());
        product = productRepository.save(product);
        return new ResponseEntity<>(new APIRespone(true, "Product added successfully", ResponseConverter.convertToProductResponse(product)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> updateProduct(Long id, ProductRequest productRequest) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();
        product.setProductName(productRequest.getProductName());
        try {
            InputStream imageInputStream = productRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(imageInputStream);
            product.setImage(base64Image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
          Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Category not found", ""), HttpStatus.NOT_FOUND);
        }

        product.setCategory(category.get());
        Optional<Store> store = storeRepository.findById(productRequest.getStoreId());
        if (store.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }
        product.getStores().add(store.get());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setBestSale(productRequest.getBestSale());
        product = productRepository.save(product);
        return new ResponseEntity<>(new APIRespone(true, "Product updated successfully", ResponseConverter.convertToProductResponse(product)), HttpStatus.OK);
    }

@Override
public ResponseEntity<APIRespone> deleteProduct(Long id) {
    Optional<Product> product = productRepository.findById(id);
    if (product.isEmpty()) {
        return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
    }
    productRepository.deleteById(id);
    return new ResponseEntity<>(new APIRespone(true, "Product deleted successfully", ""), HttpStatus.OK);

   }

}
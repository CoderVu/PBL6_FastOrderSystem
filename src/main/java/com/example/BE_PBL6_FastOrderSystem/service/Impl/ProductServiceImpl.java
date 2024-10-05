package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Category;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.ProductStore;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CategoryResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import com.example.BE_PBL6_FastOrderSystem.response.ResponseConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final ProductStoreRepository productStoreRepository;

    @Override
    public ResponseEntity<APIRespone> getAllProduct() {
        if (productRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "No product found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findAll().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponse = productRepository.findById(productId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponse), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<APIRespone> getProductsByStoreId(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        if (store.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findByStoreId(storeId).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }

    @Override
    public Long calculateOrderNowAmountProduct(Long productId, int quantity) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return null;
        }
        return (long) (product.get().getPrice() * quantity);
    }

    @Override
    public ResponseEntity<APIRespone> applyAllProductsToAllStores(int quantity) {
        List<Product> products = productRepository.findAll();
        List<Store> stores = storeRepository.findAll();
        int initialProductStoreCount = productStoreRepository.findAll().size();
        for (Product product : products) {
            if (product.getStockQuantity()<quantity) {
                return new ResponseEntity<>(new APIRespone(false, "Not enough stock quantity for product: " + product.getProductName(), ""), HttpStatus.BAD_REQUEST);
            }
            for (Store store : stores) {
                boolean storeHasProduct = store.getProductStores().stream()
                        .anyMatch(productStore -> productStore.getProduct().getProductId().equals(product.getProductId()));
                if (!storeHasProduct) {
                    ProductStore productStore = new ProductStore();
                    productStore.setProduct(product);
                    productStore.setStore(store);
                    productStore.setStockQuantity(quantity);
                    product.getProductStores().add(productStore);
                    store.getProductStores().add(productStore);
                    productStoreRepository.save(productStore);
                }
            }
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        }
        if (initialProductStoreCount == productStoreRepository.findAll().size()) {
            return new ResponseEntity<>(new APIRespone(false, "All products already applied to all stores", ""), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new APIRespone(true, "All products applied to all stores successfully", ""), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<APIRespone> applyAllProductsToStore(Long storeId, int quantity) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }
        Store store = storeOptional.get();
        List<Product> products = productRepository.findAll();
        int initialProductStoreCount = productStoreRepository.findAll().size();
        for (Product product : products) {
            if (product.getStockQuantity()<quantity) {
                return new ResponseEntity<>(new APIRespone(false, "Not enough stock quantity for product: " + product.getProductName(), ""), HttpStatus.BAD_REQUEST);
            }
            boolean storeHasProduct = store.getProductStores().stream()
                    .anyMatch(productStore -> productStore.getProduct().getProductId().equals(product.getProductId()));
            if (!storeHasProduct) {
                ProductStore productStore = new ProductStore();
                productStore.setProduct(product);
                productStore.setStore(store);
                productStore.setStockQuantity(quantity);
                product.getProductStores().add(productStore);
                store.getProductStores().add(productStore);
                productStoreRepository.save(productStore);
            }
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        }
        if (initialProductStoreCount == productStoreRepository.findAll().size()) {
            return new ResponseEntity<>(new APIRespone(false, "All products already applied to store", ""), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new APIRespone(true, "All products applied to store successfully", ""), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<APIRespone> applyProductsToStore(List<Long> productIds, Long storeId, int quantity) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }

        Store store = storeOptional.get();
        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Products not found", ""), HttpStatus.NOT_FOUND);
        }

        List<String> alreadyAppliedProducts = new ArrayList<>();
        int initialProductStoreCount = productStoreRepository.findAll().size();

        for (Product product : products) {
            if (product.getStockQuantity() < quantity) {
                return new ResponseEntity<>(new APIRespone(false, "Not enough stock quantity for product: " + product.getProductName(), ""), HttpStatus.BAD_REQUEST);
            }

            boolean storeHasProduct = store.getProductStores().stream()
                    .anyMatch(productStore -> productStore.getProduct().getProductId().equals(product.getProductId()));

            boolean productBelongsToAnotherStore = product.getProductStores().stream()
                    .anyMatch(productStore -> !productStore.getStore().getStoreId().equals(store.getStoreId()));

            if (productBelongsToAnotherStore) {
                alreadyAppliedProducts.add(product.getProductName());
                continue;
            }

            if (!storeHasProduct) {
                ProductStore productStore = new ProductStore();
                productStore.setProduct(product);
                productStore.setStore(store);
                productStore.setStockQuantity(quantity);
                product.getProductStores().add(productStore);
                store.getProductStores().add(productStore);
                productStoreRepository.save(productStore);
            }
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        }

        if (!alreadyAppliedProducts.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Some products already belong to another store: " + String.join(", ", alreadyAppliedProducts), ""), HttpStatus.BAD_REQUEST);
        }

        if (initialProductStoreCount == productStoreRepository.findAll().size()) {
            return new ResponseEntity<>(new APIRespone(false, "Products already applied to store", ""), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new APIRespone(true, "Products applied to store successfully", ""), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> applyProductToAllStores(Long productId, int quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();
        List<Store> stores = storeRepository.findAll();
        int initialProductStoreCount = product.getProductStores().size();
        if (product.getStockQuantity()<quantity) {
            return new ResponseEntity<>(new APIRespone(false, "Not enough stock quantity for product: " + product.getProductName(), ""), HttpStatus.BAD_REQUEST);
        }

        for (Store store : stores) {
            boolean storeHasProduct = store.getProductStores().stream()
                    .anyMatch(productStore -> productStore.getProduct().getProductId().equals(productId));
            if (!storeHasProduct) {
                ProductStore productStore = new ProductStore();
                productStore.setProduct(product);
                productStore.setStore(store);
                productStore.setStockQuantity(quantity);
                product.getProductStores().add(productStore);
                store.getProductStores().add(productStore);
                productStoreRepository.save(productStore);
            }
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        }

        if (initialProductStoreCount == product.getProductStores().size()) {
            return new ResponseEntity<>(new APIRespone(false, "Product already applied to all stores", ""), HttpStatus.BAD_REQUEST);
        }
        productRepository.save(product);
        return new ResponseEntity<>(new APIRespone(true, "Product applied to all stores successfully", ""), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<APIRespone> removeProductFromStore(Long storeId, Long productId) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Store not found", ""), HttpStatus.NOT_FOUND);
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Product not found", ""), HttpStatus.NOT_FOUND);
        }

        Store store = storeOptional.get();
        Product product = productOptional.get();

        store.getProductStores().removeIf(ps -> ps.getProduct().equals(product));
        product.getProductStores().removeIf(ps -> ps.getStore().equals(store));

        storeRepository.save(store);
        productRepository.save(product);
        product.setStockQuantity(product.getStockQuantity() + product.getProductStores().stream().mapToInt(ProductStore::getStockQuantity).sum());
        productRepository.save(product);

        return new ResponseEntity<>(new APIRespone(true, "Product removed from store successfully", ""), HttpStatus.OK);
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
    public ResponseEntity<APIRespone> getBestSaleProduct() {
        if (productRepository.findByBestSale(true).isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "No best sale product found", ""), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = productRepository.findByBestSale(true).stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", productResponses), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<APIRespone> addProduct(ProductRequest productRequest) {
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
            return new ResponseEntity<>(new APIRespone(false, "Error when upload image", ""), HttpStatus.BAD_REQUEST);
        }
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Category not found", ""), HttpStatus.NOT_FOUND);
        }
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(category.get());
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
        if (productRepository.existsByProductName(productRequest.getProductName()) && !product.getProductName().equals(productRequest.getProductName())) {
            return new ResponseEntity<>(new APIRespone(false, "Product already exists", ""), HttpStatus.BAD_REQUEST);
        }
        product.setProductName(productRequest.getProductName());
        if (productRequest.getImage() != null) {
            try {
                InputStream imageInputStream = productRequest.getImage().getInputStream();
                String base64Image = ImageGeneral.fileToBase64(imageInputStream);
                product.setImage(base64Image);
            } catch (IOException e) {
                return new ResponseEntity<>(new APIRespone(false, "Error when upload image", ""), HttpStatus.BAD_REQUEST);
            }
        }
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Category not found", ""), HttpStatus.NOT_FOUND);
        }
        product.setCategory(category.get());
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
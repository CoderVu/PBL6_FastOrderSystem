package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.request.CartComboRequest;
import com.example.BE_PBL6_FastOrderSystem.request.CartRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CartItemsResponse;
import com.example.BE_PBL6_FastOrderSystem.service.ICartService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    final private UserRepository userRepository;

    final private ProductRepository productRepository;

    final private CartItemRepository cartItemRepository;
    final private StoreRepository storeRepository;
    final private ComboRepository comboRepository;

    @Override
    public ResponseEntity<APIRespone> addProductToCart(Long userId, CartRequest cartRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        Product product = productRepository.findById(cartRequest.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        if (cartRequest.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Quantity must be greater than 0", ""));
        }
        Store store = product.getStores().stream()
                .filter(s -> s.getStoreId().equals(cartRequest.getStoreId()))
                .findFirst()
                .orElse(null);
        if (store == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product does not belong to the specified store", ""));
        }
        Cart cartItem = new Cart();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(cartRequest.getQuantity());
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setTotalPrice(product.getPrice() * cartRequest.getQuantity());
        cartItem.setStoreId(cartRequest.getStoreId());
        cartItem.setStatus(cartRequest.getStatus());
        cartItemRepository.save(cartItem);;
        return ResponseEntity.ok(new APIRespone(true, "Add to cart successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> addComboToCart(Long userId, CartComboRequest cartComboRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        // Lay combo tu combo id
        Combo combo = comboRepository.findById(cartComboRequest.getComboId()).orElse(null);
        if (combo == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        // Kiem tra so luong cua tung product trong combo
        for (Product product : combo.getProducts()) {
            if (product.getStockQuantity() < cartComboRequest.getQuantity()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product " + product.getProductName() + " not enough", ""));
            }
        }
        // Kiem tra store cua tung product trong combo
        for (Product product : combo.getProducts()) {
            Store store = storeRepository.findById(cartComboRequest.getStoreId()).orElse(null);
            if (store == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
            }
            if (!product.getStores().contains(store)) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product " + product.getProductName() + " does not belong to the specified store", ""));
            }
        }
        // Them combo vao cart
        Cart cartItem = new Cart();
        cartItem.setUser(user);
        cartItem.setCombo(combo); // Set combo
        cartItem.setQuantity(cartComboRequest.getQuantity());
        cartItem.setUnitPrice(combo.getComboPrice());
        cartItem.setTotalPrice(combo.getComboPrice() * cartComboRequest.getQuantity());
        cartItem.setStoreId(cartComboRequest.getStoreId());
        cartItem.setStatus(cartComboRequest.getStatus());
        cartItemRepository.save(cartItem);

        return ResponseEntity.ok(new APIRespone(true, "Add combo to cart successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> getHistoryCart(Long userId) {
        List<Cart> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No cart items found for the user", ""));
        }
        List<CartItemsResponse> cartItemResponses = cartItems.stream()
                .map(cartItem -> new CartItemsResponse(
                        cartItem.getCartId(),
                        cartItem.getUser().getId(),
                        cartItem.getProduct().getProductId(),
                        cartItem.getProduct().getProductName(),
                        cartItem.getProduct().getImage(),
                        cartItem.getQuantity(),
                        cartItem.getUnitPrice(),
                        cartItem.getTotalPrice(),
                        cartItem.getStoreId(),
                        cartItem.getStatus(),
                        cartItem.getCreatedAt(),
                        cartItem.getUpdatedAt()
                ))
                .toList();
        return ResponseEntity.ok(new APIRespone(true, "Cart items retrieved successfully", cartItemResponses));
    }

}

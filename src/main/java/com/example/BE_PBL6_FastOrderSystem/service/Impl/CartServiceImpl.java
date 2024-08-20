package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.CartItemRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.CartRequest;
import com.example.BE_PBL6_FastOrderSystem.service.ICartService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    final private UserRepository userRepository;

    final private ProductRepository productRepository;

    final private CartItemRepository cartItemRepository;

    @Override
    public void addToCart(Long userId, CartRequest cartRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Store store = product.getStores().stream()
                .filter(s -> s.getStoreId().equals(cartRequest.getStoreId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product does not belong to the specified store"));

        if (!product.getStores().contains(store)) {
            throw new ResourceNotFoundException("Product does not belong to the specified store");
        }

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(cartRequest.getQuantity());
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setTotalPrice(product.getPrice() * cartRequest.getQuantity());
        cartItem.setStoreId(cartRequest.getStoreId());
        cartItem.setStatus(cartRequest.getStatus());
        cartItem.setDeliveryAddress(cartRequest.getDeliveryAddress());

        cartItemRepository.save(cartItem);
    }
}
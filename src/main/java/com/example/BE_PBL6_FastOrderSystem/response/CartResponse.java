package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
public class CartResponse {
    private Long cartId;
    private String type;
    private CartProductResponse product;
    private CartComboResponse combo;
    public CartResponse(Long cartId, CartProductResponse product, CartComboResponse combo) {
        this.cartId = cartId;
        if (product != null) {
            this.type = "product";
            this.product = product;
        } else if (combo != null) {
            this.type = "combo";
            this.combo = combo;
        }
    }

    public static List<CartResponse> convertListCartToCartResponse(List<Cart> cartItems) {
        return cartItems.stream().map(cart -> {
            if (cart.getProduct() != null) {
                CartProductResponse productResponse = new CartProductResponse(
                        cart.getUser().getId(),
                        cart.getProduct().getProductId(),
                        cart.getProduct().getProductName(),
                        cart.getProduct().getImage(),
                        cart.getQuantity(),
                        cart.getUnitPrice(),
                        cart.getTotalPrice(),
                        cart.getSize().getName(),
                        cart.getStoreId(),
                        cart.getStatus(),
                        cart.getCreatedAt(),
                        cart.getUpdatedAt()
                );
                return new CartResponse(cart.getCartId(), productResponse, null);
            } else if (cart.getCombo() != null) {
                CartComboResponse comboResponse = new CartComboResponse(
                        cart.getUser().getId(),
                        cart.getCombo().getComboId(),
                        cart.getCombo().getComboName(),
                        cart.getCombo().getImage(),
                        cart.getQuantity(),
                        cart.getUnitPrice(),
                        cart.getTotalPrice(),
                        cart.getSize().getName(),
                        cart.getStoreId(),
                        cart.getStatus(),
                        cart.getCreatedAt(),
                        cart.getUpdatedAt()
                );
                return new CartResponse(cart.getCartId(), null, comboResponse);
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }
}
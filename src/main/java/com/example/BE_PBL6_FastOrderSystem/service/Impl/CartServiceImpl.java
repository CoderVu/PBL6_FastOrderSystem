package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.request.CartComboRequest;
import com.example.BE_PBL6_FastOrderSystem.request.CartProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.CartResponse;
import com.example.BE_PBL6_FastOrderSystem.service.ICartService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    final private UserRepository userRepository;
    final private ProductRepository productRepository;
    final private CartItemRepository cartItemRepository;
    final private StoreRepository storeRepository;
    final private ComboRepository comboRepository;
    final private SizeRepository sizeRepository;

    @Override
    public ResponseEntity<APIRespone> addProductToCart(Long userId, CartProductRequest cartProductRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        Product product = productRepository.findById(cartProductRequest.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        if (cartProductRequest.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Quantity must be greater than 0", ""));
        }
        if (product.getStockQuantity() < cartProductRequest.getQuantity()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not enough", ""));
        }
        ProductStore productStore = product.getProductStores().stream()
                .filter(ps -> Objects.equals(ps.getStore().getStoreId(), cartProductRequest.getStoreId()))
                .findFirst()
                .orElse(null);
        Store store = (productStore != null) ? productStore.getStore() : null;
        if (store == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product does not belong to the specified store", ""));
        }
        Cart cartItem = cartItemRepository.findByUserIdAndProductIdAndSizeAndStoreId(userId, cartProductRequest.getProductId(), cartProductRequest.getSize(), cartProductRequest.getStoreId());
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + cartProductRequest.getQuantity());
            cartItem.setTotalPrice(cartItem.getUnitPrice() * cartItem.getQuantity());
        } else {
            cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartProductRequest.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setTotalPrice(product.getPrice() * cartProductRequest.getQuantity());

            Size size = sizeRepository.findByName(cartProductRequest.getSize());
            if (size == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Size not found", ""));
            }
            cartItem.setSize(size);
            cartItem.setStoreId(cartProductRequest.getStoreId());
            cartItem.setStatus(cartProductRequest.getStatus());
        }
        cartItemRepository.save(cartItem);
        return ResponseEntity.ok(new APIRespone(true, "Add to cart successfully", cartItem.getCartId().toString()));
    }

    @Override
    public ResponseEntity<APIRespone> addComboToCart(Long userId, CartComboRequest cartComboRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        Combo combo = comboRepository.findById(cartComboRequest.getComboId()).orElse(null);
        if (combo == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        List<Product> drinkProducts = new ArrayList<>();
        for (Long drinkId : cartComboRequest.getDrinkId()) {
            Product drinkProduct = productRepository.findById(drinkId).orElse(null);
            if (drinkProduct == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Drink product not found", ""));
            }
            if (drinkProduct.getStockQuantity() < cartComboRequest.getQuantity()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Drink product not enough", ""));
            }
            ProductStore drinkProductStore = drinkProduct.getProductStores().stream()
                    .filter(ps -> Objects.equals(ps.getStore().getStoreId(), cartComboRequest.getStoreId()))
                    .findFirst()
                    .orElse(null);
            if (drinkProductStore == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Drink product does not belong to the specified store", ""));
            }
            drinkProducts.add(drinkProduct);
        }

        for (Product product : combo.getProducts()) {
            if (product.getStockQuantity() < cartComboRequest.getQuantity()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product " + product.getProductName() + " not enough", ""));
            }
        }
        for (Product product : combo.getProducts()) {
            Store store = storeRepository.findById(cartComboRequest.getStoreId()).orElse(null);
            if (store == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
            }
            if (product.getProductStores().stream().noneMatch(ps -> ps.getStore().equals(store))) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product " + product.getProductName() + " does not belong to the specified store", ""));
            }
        }
        Cart cartItem = cartItemRepository.findByUserIdAndComboIdAndSizeAndStoreId(userId, cartComboRequest.getComboId(), cartComboRequest.getSize(), cartComboRequest.getStoreId());
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + cartComboRequest.getQuantity());
            cartItem.setTotalPrice(cartItem.getUnitPrice() * cartItem.getQuantity());
        } else {
            cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setCombo(combo);
            cartItem.setDrinkProducts(drinkProducts);
            cartItem.setQuantity(cartComboRequest.getQuantity());
            cartItem.setUnitPrice(combo.getComboPrice());
            cartItem.setTotalPrice(combo.getComboPrice() * cartComboRequest.getQuantity());
            Size size = sizeRepository.findByName(cartComboRequest.getSize());
            if (size == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Size not found", ""));
            }
            cartItem.setSize(size);
            cartItem.setStoreId(cartComboRequest.getStoreId());
            cartItem.setStatus(cartComboRequest.getStatus());
        }
        cartItemRepository.save(cartItem);

        return ResponseEntity.ok(new APIRespone(true, "Add combo to cart successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getHistoryCart(Long userId) {
        List<Cart> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No cart items found for the user", ""));
        }
        List<CartResponse> cartResponses = CartResponse.convertListCartToCartResponse(cartItems);
        return ResponseEntity.ok(new APIRespone(true, "Get history cart successfully", cartResponses));
    }

    @Override
    public ResponseEntity<APIRespone> deleteCart(Long userId, Long cartId) {
        Cart cartItem = cartItemRepository.findByUserIdAndCartId(userId, cartId);
        if (cartItem == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Cart item not found", ""));
        }
        cartItemRepository.delete(cartItem);
        return ResponseEntity.ok(new APIRespone(true, "Delete cart item successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> updateCart(Long userId, Long cartId, Integer quantity) {
        Cart cartItem = cartItemRepository.findByUserIdAndCartId(userId, cartId);
        if (cartItem == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Cart item not found", ""));
        }
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Quantity must be greater than 0", ""));
        }
        if (cartItem.getProduct() != null) {
            if (cartItem.getProduct().getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not enough", ""));
            }
        }
        if (cartItem.getCombo() != null) {
            for (Product product : cartItem.getCombo().getProducts()) {
                if (product.getStockQuantity() < quantity) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "Product " + product.getProductName() + " not enough", ""));
                }
            }
        }
        Integer currentQuantity = cartItem.getQuantity();
        cartItem.setQuantity(currentQuantity + quantity);
        cartItem.setTotalPrice(cartItem.getUnitPrice() * quantity);
        cartItemRepository.save(cartItem);
        return ResponseEntity.ok(new APIRespone(true, "Update cart item successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getAllStoreCart(Long userId) {
        List<Cart> cartItems = cartItemRepository.findByUserId(userId);
        ArrayList<Long> storeIds = new ArrayList<>();
        for (Cart cart : cartItems) {
            if (!storeIds.contains(cart.getStoreId())) {
                storeIds.add(cart.getStoreId());
            }
        }
        return ResponseEntity.ok(new APIRespone(true, "Get liststore successfully", storeIds));
    }

    @Override
    public ResponseEntity<APIRespone> getCartByStore(Long userId, Long storeId) {
        List<Cart> cartItems = cartItemRepository.findByUserId(userId);
        List<Cart> cartByStoreId = new ArrayList<>();
        for (Cart cart : cartItems) {
            if (cart.getStoreId().equals(storeId)) {
                cartByStoreId.add(cart);
            }
        }
        List<CartResponse> cartResponses = CartResponse.convertListCartToCartResponse(cartByStoreId);
        return ResponseEntity.ok(new APIRespone(true, "Get history cart successfully", cartResponses));
    }

    @Override
    public ResponseEntity<APIRespone> getCartById(Long userId, Long cartid) {
        List<Cart> cartItems = cartItemRepository.findByUserId(userId);
        List<Cart> cartList = new ArrayList<>();
        for (Cart cart : cartItems) {
            if (cart.getCartId().equals(cartid)) {
                cartList.add(cart);
                break;
            }
        }
        List<CartResponse> cartResponses = CartResponse.convertListCartToCartResponse(cartList);
        return ResponseEntity.ok(new APIRespone(true, "Get history cart successfully", cartResponses));
    }
}

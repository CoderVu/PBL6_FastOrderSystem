package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.request.CartComboRequest;
import com.example.BE_PBL6_FastOrderSystem.request.CartProductRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/cart")
public class UserCartController {

    private final ICartService cartService;

    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }

    @PostMapping("/add/product")
    public  ResponseEntity<APIRespone> addToCart(@RequestBody CartProductRequest cartProductRequest) {
        Long userId = getCurrentUserId();
        return cartService.addProductToCart(userId, cartProductRequest);
    }
    @PostMapping("/add/combo")
    public ResponseEntity<APIRespone> addToCartCombo(@RequestBody CartComboRequest cartComboRequest) {
        Long userId = getCurrentUserId();
        return cartService.addComboToCart(userId, cartComboRequest);
    }
    @GetMapping("/history")
    public ResponseEntity<APIRespone> getHistoryCart() {
        Long userId = getCurrentUserId();
        return cartService.getHistoryCart(userId);
    }
    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<APIRespone> deleteCart(@PathVariable Long cartId) {
        Long userId = getCurrentUserId();
        return cartService.deleteCart(userId, cartId);
    }
    @PutMapping("/update")
    public ResponseEntity<APIRespone> updateCart(@RequestParam Long cartId, @RequestParam Integer quantity) {
        Long userId = getCurrentUserId();
        return cartService.updateCart(userId, cartId, quantity);
    }
    @GetMapping("/allstore")
    public ResponseEntity<APIRespone> getAllStoreCart() {
        Long userId = getCurrentUserId();
        return cartService.getAllStoreCart(userId);
    }
    @GetMapping("/store/{storeid}")
    public ResponseEntity<APIRespone> getCartByStore(@PathVariable Long storeid) {
        Long userId = getCurrentUserId();
        return cartService.getCartByStore(userId,storeid);
    }
    @GetMapping("/{cartid}")
    public ResponseEntity<APIRespone> getCartById(@PathVariable Long cartid) {
        Long userId = getCurrentUserId();
        return cartService.getCartById(userId,cartid);
    }
}
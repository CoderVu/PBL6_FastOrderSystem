package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.request.CartRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.ICartService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/cart")
public class CartController {

    private final ICartService cartService;

    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }

    @PostMapping("/add")
    public  ResponseEntity<APIRespone> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam Long storeId) {
        Long userId = getCurrentUserId();
        CartRequest cartRequest = new CartRequest(productId, quantity, storeId, "PENDING");
        return cartService.addToCart(userId, cartRequest);
    }
    @GetMapping("/history")
    public ResponseEntity<APIRespone> getHistoryCart() {
        Long userId = getCurrentUserId();
        return cartService.getHistoryCart(userId);
    }
}
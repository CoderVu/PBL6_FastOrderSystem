package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.request.CartRequest;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.ICartService;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/cart")
public class CartController {

    private final ICartService cartService;

    private final IOrderService orderService;

    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam Long storeId,
            @RequestParam String status,
            @RequestParam String deliveryAddress) {
        Long userId = getCurrentUserId();
        try {
            CartRequest cartRequest = new CartRequest(productId, quantity, storeId, status, deliveryAddress);
            cartService.addToCart(userId, cartRequest);
            return ResponseEntity.ok().body("Product added to cart successfully");
        }  catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
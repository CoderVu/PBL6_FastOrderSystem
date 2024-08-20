package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.exception.OrderNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class UserOrderController {
    private final IOrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestParam String paymentMethod,
            @RequestParam Long cartId) {
            Long userId = getCurrentUserId();
        try {
            OrderResponse orderResponse = orderService.placeOrder(userId, paymentMethod, cartId);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/history/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        Long userId = getCurrentUserId();
        List<OrderResponse> orders = orderService.getAllOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }
    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }

    @GetMapping("/history/{orderId}")
    public ResponseEntity getOrderById(@PathVariable Long orderId) {
        try {
            Long userId = getCurrentUserId();
            OrderResponse orderResponse = orderService.getOrderByIdAndUserId(orderId, userId);
            return ResponseEntity.ok(orderResponse);
        }
        catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching order");
        }
    }
}
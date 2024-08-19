package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/order")
@RequiredArgsConstructor
public class OwnerOrderController {
    private final IOrderService orderService;

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            Long ownerId = FoodUserDetails.getCurrentUserId();
            String message = orderService.updateOrderStatus(orderId, ownerId, status);
            return ResponseEntity.ok(message);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Forbidden status code
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
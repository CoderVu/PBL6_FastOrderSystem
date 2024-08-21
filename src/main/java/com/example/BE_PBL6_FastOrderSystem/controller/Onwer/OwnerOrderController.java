package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
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
    public ResponseEntity<APIRespone> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
            Long ownerId = FoodUserDetails.getCurrentUserId();
            return orderService.updateOrderStatus(orderId, ownerId, status);
    }
}
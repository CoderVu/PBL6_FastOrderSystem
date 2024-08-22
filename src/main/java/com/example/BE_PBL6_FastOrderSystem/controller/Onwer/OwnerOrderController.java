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

    @PutMapping("/update-status")
    public ResponseEntity<APIRespone> updateOrderStatus(
            @RequestParam String orderCode,
            @RequestParam String status) {
            Long ownerId = FoodUserDetails.getCurrentUserId();
            return orderService.updateOrderStatusOfOwner(orderCode, ownerId, status);
    }
    @GetMapping("/get-all")
    public  ResponseEntity<APIRespone> getAllOrders() {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        return orderService.getAllOrdersByOwner(ownerId);

    }
}
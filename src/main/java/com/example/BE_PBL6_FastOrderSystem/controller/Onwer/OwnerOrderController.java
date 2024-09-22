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
    @GetMapping("/get-all")
    public ResponseEntity<APIRespone> getAllOrder() {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        System.out.println(ownerId);
        return orderService.getAllOrderDetailOfStore(ownerId);
    }
    @GetMapping("/get-by-code")
    public ResponseEntity<APIRespone> getOrderByCode(@RequestParam String orderCode) {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        return orderService.getOrderDetailOfStore(ownerId, orderCode);
    }
    @PutMapping("/update-status")
    public ResponseEntity<APIRespone> updateOrderStatus(
            @RequestParam String orderCode,
            @RequestParam String status) {
            Long ownerId = FoodUserDetails.getCurrentUserId();
            return orderService.updateStatusDetail(orderCode, ownerId, status);
    }

}
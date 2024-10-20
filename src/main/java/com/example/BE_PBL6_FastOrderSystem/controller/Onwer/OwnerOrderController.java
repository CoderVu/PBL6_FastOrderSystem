package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.request.UpdateQuantityRequest;
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
    @GetMapping("")
    public ResponseEntity<APIRespone> getAllOrder() {
        Long OwnerId = FoodUserDetails.getCurrentUserId();
        return orderService.getAllOrderDetailOfStore(OwnerId);
    }
    @GetMapping("/get-by-code")
    public ResponseEntity<APIRespone> getOrderByCode(@RequestParam String orderCode) {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        return orderService.getOrderDetailOfStore(ownerId, orderCode);
    }
    @PutMapping("/update-status")
    public ResponseEntity<APIRespone> updateOrderStatus(
            @RequestParam String orderCode,
            @RequestParam String status,
            @RequestParam Long storeId ) {
            return orderService.updateOrderDetailStatus(orderCode, storeId, status);
    }
    @GetMapping("/status")
    public ResponseEntity<APIRespone> getOrderByStatus(@RequestParam String status) {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        return orderService.getOrderByStatus(ownerId, status);
    }
//    @PutMapping("/update-quantity")
//    public ResponseEntity<APIRespone> updateQuantityProduct(
//            @RequestParam(required = false) Long productId,
//            @RequestParam(required = false) Long comboId,
//            @RequestParam Long storeId,
//            @RequestParam int quantity) {
//        return orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
//    }


}
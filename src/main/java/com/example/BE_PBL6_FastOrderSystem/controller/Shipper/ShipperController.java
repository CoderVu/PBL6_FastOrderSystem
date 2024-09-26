package com.example.BE_PBL6_FastOrderSystem.controller.Shipper;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IShipperOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/shipper")
public class ShipperController {
    private final IShipperOrderService shipperOrderService;
    private final IUserService userService;
    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }
    @PostMapping("/order/approve/{shipperOrderId}")
    public ResponseEntity<APIRespone> approveShipperOrder(@PathVariable Long shipperOrderId, @RequestParam Boolean isAccepted){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.approveShipperOrder(shipperId,shipperOrderId,isAccepted);
    }
    @GetMapping("/order/{shipperOrderId}")
    public ResponseEntity<APIRespone> getShipperOrderbyId (@PathVariable Long shipperOrderId){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.getShipperOrderbyId(shipperId,shipperOrderId);

    }
    @PostMapping("/order/update-status/{shipperOrderId}")
    public ResponseEntity<APIRespone> updateStatusOrder(@PathVariable Long shipperOrderId, @RequestParam Long OderDetailId){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.updateStatusOrderDetail(shipperId,shipperOrderId, OderDetailId);
    }
}

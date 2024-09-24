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
    @GetMapping("/order")
    public ResponseEntity<APIRespone> getShipperOrderbyOderDetailId (){
        Long shipperId= getCurrentUserId();
        System.out.println(shipperId);
        return shipperOrderService.getShipperOrderbyOderDetailId(shipperId);
    }
  @GetMapping("/order/{shipperOrderId}")
    public ResponseEntity<APIRespone> getShipperOrderbyId (@PathVariable Long shipperOrderId){
        return shipperOrderService.getShipperOrderbyId(shipperOrderId);
    }
}

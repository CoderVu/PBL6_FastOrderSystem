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
    @GetMapping("/order/all")
    public ResponseEntity<APIRespone> getAllOrder(){
        return shipperOrderService.getAll();
    } // lay ra tat ca don hang
    @GetMapping("/order/all-one-shipper")
    public ResponseEntity<APIRespone> getAllShipperOrder(){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.getAllShipperOrder(shipperId);
    } // lay ra tat ca don hang cua 1 shipper
    @GetMapping("/order/sorted-by-distance")
    public ResponseEntity<APIRespone> getOrdersSortedByDistance(@RequestParam int page, @RequestParam int size){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.getOrdersSortedByDistance(shipperId,page,size);
    } // lay ra tat ca don hang co the nhan
    @PostMapping("/location")
    public ResponseEntity<APIRespone> updateShipperLocation(@RequestParam Double newLatitude, @RequestParam Double newLongitude){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.updateShipperLocation(shipperId,newLatitude,newLongitude);
    } // cap nhat vi tri cua shipper
    @GetMapping("/order/{shipperOrderId}")
    public ResponseEntity<APIRespone> getShipperOrderbyId (@PathVariable Long shipperOrderId){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.getShipperOrderbyId(shipperId,shipperOrderId);
    } // lay ra 1 don hang cu the cua 1 shipper
    @PostMapping("/order/approve/{shipperOrderId}")
    public ResponseEntity<APIRespone> approveShipperOrder(@PathVariable Long shipperOrderId, @RequestParam Boolean isAccepted){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.approveShipperOrder(shipperId,shipperOrderId,isAccepted);
    }
    @PostMapping("/order/update-status/{shipperOrderId}")
    public ResponseEntity<APIRespone> updateStatusOrder(@PathVariable Long shipperOrderId, @RequestParam Long OderDetailId){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.updateStatusOrderDetail(shipperId,shipperOrderId, OderDetailId);
    }
    @PostMapping("/order/finish-delivery/{shipperOrderId}")
    public ResponseEntity<APIRespone> finishDelivery(@PathVariable Long shipperOrderId, @RequestParam Long OderDetailId){
        Long shipperId= getCurrentUserId();
        return shipperOrderService.finishDelivery(shipperId,shipperOrderId, OderDetailId);
    }
}

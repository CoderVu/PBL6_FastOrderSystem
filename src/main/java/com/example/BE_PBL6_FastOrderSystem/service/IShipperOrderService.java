package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IShipperOrderService {

    // lay ra 1 don hang cu the
    ResponseEntity<APIRespone> getShipperOrderbyId(Long shipperId, Long shipperOrderId);
    ResponseEntity<APIRespone> approveShipperOrder(Long shipperId, Long shipperOrderId, Boolean isAccepted);

    ResponseEntity<APIRespone> updateStatusOrderDetail(Long shipperId, Long shipperOrderId, Long orderDetailId);
}

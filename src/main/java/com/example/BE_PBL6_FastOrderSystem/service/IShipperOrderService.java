package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IShipperOrderService {
    ResponseEntity<APIRespone> getAll();
    ResponseEntity<APIRespone> getShipperOrderbyId(Long shipperId, Long shipperOrderId);
    ResponseEntity<APIRespone> getAllShipperOrder(Long shipperId);

    ResponseEntity<APIRespone> updateShipperLocation(Long shipperId, Double newLatitude, Double newLongitude);

    ResponseEntity<APIRespone> approveShipperOrder(Long shipperId, Long shipperOrderId, Boolean isAccepted);

    ResponseEntity<APIRespone> updateStatusOrderDetail(Long shipperId, Long shipperOrderId, Long orderDetailId);

    ResponseEntity<APIRespone> finishDelivery(Long shipperId, Long shipperOrderId, Long orderDetailId);



}

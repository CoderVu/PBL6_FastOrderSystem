package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IShipperOrderService {
    ResponseEntity<APIRespone> getShipperOrderbyOderDetailId(Long shipperId);

    // lay ra 1 don hang cu the
    ResponseEntity<APIRespone> getShipperOrderbyId(Long shipperOrderId);
}

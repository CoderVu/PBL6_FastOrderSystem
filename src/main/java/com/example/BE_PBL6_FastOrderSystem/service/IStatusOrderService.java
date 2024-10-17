package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.StatusOrderRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IStatusOrderService {
    ResponseEntity<APIRespone> getAllStatus();

    ResponseEntity<APIRespone> getStatusById(Long statusId);

    ResponseEntity<APIRespone> createStatus(StatusOrderRequest statusOrderRequest);

    ResponseEntity<APIRespone> updateStatus(Long statusId, StatusOrderRequest statusOrderRequest);

    ResponseEntity<APIRespone> deleteStatus(Long statusId);
}

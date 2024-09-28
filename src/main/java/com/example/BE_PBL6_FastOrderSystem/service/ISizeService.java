package com.example.BE_PBL6_FastOrderSystem.service;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;

public interface ISizeService {
    ResponseEntity<APIRespone> getSizeById(Long sizeId);
    ResponseEntity<APIRespone> getAllSizes();
    
}

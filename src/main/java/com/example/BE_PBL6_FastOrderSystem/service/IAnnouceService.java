package com.example.BE_PBL6_FastOrderSystem.service;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IAnnouceService {
    ResponseEntity<APIRespone> getbyuserId(Long userId);
    ResponseEntity<APIRespone> addnewannounce(Long userId,String title,String content);
}
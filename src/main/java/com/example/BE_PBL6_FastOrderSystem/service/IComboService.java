package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IComboService {
    ResponseEntity<APIRespone> getAllCombos();
    ResponseEntity<APIRespone> getProductsByComboId(Long comboId);
}

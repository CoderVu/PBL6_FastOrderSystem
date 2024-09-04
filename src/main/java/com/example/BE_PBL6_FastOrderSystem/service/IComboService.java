package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import com.example.BE_PBL6_FastOrderSystem.request.ComboRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

public interface IComboService {
    ResponseEntity<APIRespone> getAllCombos();
    ResponseEntity<APIRespone> getProductsByComboId(Long comboId);

    ResponseEntity<APIRespone> addCombo(ComboRequest comboRequest);

    ResponseEntity<APIRespone> updateCombo(Long comboId, ComboRequest comboRequest);

    ResponseEntity<APIRespone> deleteCombo(Long comboId);

    ResponseEntity<APIRespone> addProduct(Long comboId, Long productId);
}

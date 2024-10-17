package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.ComboRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IComboService {
    ResponseEntity<APIRespone> getAllCombos();
    ResponseEntity<APIRespone> getProductsByComboId(Long comboId);

    ResponseEntity<APIRespone> getCombosByStoreId(Long storeId);

    ResponseEntity<APIRespone> addCombo(ComboRequest comboRequest);

    ResponseEntity<APIRespone> updateCombo(Long comboId, ComboRequest comboRequest);

    ResponseEntity<APIRespone> deleteCombo(Long comboId);

    ResponseEntity<APIRespone> addProduct(Long comboId, Long productId);

    ResponseEntity<APIRespone> addProducts(Long comboId, List<Long> productIds);

    ResponseEntity<APIRespone> getComboById(Long comboId);
}

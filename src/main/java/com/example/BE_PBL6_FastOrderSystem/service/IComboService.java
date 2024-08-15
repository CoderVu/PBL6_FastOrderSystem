package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ProductResponse;

import java.util.List;

public interface IComboService {
    List<ComboResponse> getAllCombos();
    List<ProductResponse> getProductsByComboId(Long comboId);
}

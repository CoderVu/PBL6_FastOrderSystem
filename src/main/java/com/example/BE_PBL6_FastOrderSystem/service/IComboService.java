package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.dto.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.dto.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Combo;

import java.util.List;

public interface IComboService {
    List<ComboResponse> getAllCombos();
    List<ProductResponse> getProductsByComboId(Long comboId);
}

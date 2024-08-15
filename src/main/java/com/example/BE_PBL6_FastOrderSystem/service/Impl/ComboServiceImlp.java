package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.dto.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.dto.ProductResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import com.example.BE_PBL6_FastOrderSystem.repo.repository.ComboRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import com.example.BE_PBL6_FastOrderSystem.util.ResponseConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComboServiceImlp implements IComboService {
    private final ComboRepository comboRepository;

    @Override
    public List<ComboResponse> getAllCombos() {
        return comboRepository.findAll().stream()
                .map(this::convertToComboResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByComboId(Long comboId) {
        Combo combo = comboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Combo not found"));
        return combo.getProducts().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
    }

    public ComboResponse convertToComboResponse(Combo combo) {
        return new ComboResponse(
                combo.getComboId(),
                combo.getComboName(),
                combo.getComboPrice(),
                combo.getImage()
        );
    }
}
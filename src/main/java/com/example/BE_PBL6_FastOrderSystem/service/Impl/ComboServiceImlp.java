package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ComboResponse;
import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import com.example.BE_PBL6_FastOrderSystem.repository.ComboRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComboServiceImlp implements IComboService {
    private final ComboRepository comboRepository;

    @Override
    public ResponseEntity<APIRespone> getAllCombos() {
        if (comboRepository.findAll().isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No combo found", ""));
        }
        List<ComboResponse> comboResponses = comboRepository.findAll().stream()
                .map(this::convertToComboResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", comboResponses));
    }

    @Override
    public ResponseEntity<APIRespone> getProductsByComboId(Long comboId) {
      if (comboRepository.findById(comboId).isEmpty()) {
          return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
      }
        Combo combo = comboRepository.findById(comboId).get();
        return ResponseEntity.ok(new APIRespone(true, "Success", convertToComboResponse(combo)));
    }

    @Override
    public Combo findBycomboId(Long comboId) {
        return comboRepository.findById(comboId).get();
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
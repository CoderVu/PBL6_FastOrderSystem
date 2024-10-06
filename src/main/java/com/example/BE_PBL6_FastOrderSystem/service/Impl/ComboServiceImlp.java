package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.request.ComboRequest;
import com.example.BE_PBL6_FastOrderSystem.response.*;
import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import com.example.BE_PBL6_FastOrderSystem.repository.ComboRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import com.example.BE_PBL6_FastOrderSystem.response.ResponseConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComboServiceImlp implements IComboService {
    private final ComboRepository comboRepository;
    private final ProductRepository productRepository;

    @Override
    public ResponseEntity<APIRespone> getAllCombos() {
        if (comboRepository.findAll().isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No combo found", ""));
        }
        List<ComboResponse> comboResponses = comboRepository.findAll().stream()
                .map(ResponseConverter::convertToComboResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", comboResponses));
    }

    @Override
    public ResponseEntity<APIRespone> getProductsByComboId(Long comboId) {
        if (comboRepository.findById(comboId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        Combo combo = comboRepository.findById(comboId).get();
        List<ProductResponse> productResponses = combo.getProducts().stream()
                .map(ResponseConverter::convertToProductResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", productResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getCombosByStoreId(Long storeId) {
        List<Combo> combos = comboRepository.findCombosByStoreId(storeId);
        if (combos.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No combo found", ""));
        }
        List<ComboResponse> comboResponses = combos.stream()
                .map(ResponseConverter::convertToComboResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", comboResponses));
    }
    @Override
    public ResponseEntity<APIRespone> addCombo(ComboRequest comboRequest) {
        if (comboRepository.existsByComboName(comboRequest.getComboName())) {
          return ResponseEntity.badRequest().body(new APIRespone(false, "Combo name already exists", ""));
        }
        Combo combo = new Combo();
        combo.setComboName(comboRequest.getComboName());
        combo.setComboPrice(comboRequest.getPrice());
        try {
            InputStream image = comboRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(image);
            combo.setImage(base64Image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        combo.setDescription(comboRequest.getDescription());
        comboRepository.save(combo);
       return ResponseEntity.status(HttpStatus.CREATED).body(new APIRespone(true, "Combo added successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> updateCombo(Long comboId, ComboRequest comboRequest) {
        if (comboRepository.findById(comboId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        Combo combo = comboRepository.findById(comboId).get();
        combo.setComboName(comboRequest.getComboName());
        combo.setComboPrice(comboRequest.getPrice());
        try {
            InputStream image = comboRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(image);
            combo.setImage(base64Image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        combo.setDescription(comboRequest.getDescription());
        comboRepository.save(combo);
        return ResponseEntity.ok(new APIRespone(true, "Combo updated successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> deleteCombo(Long comboId) {
        if (comboRepository.findById(comboId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        comboRepository.deleteById(comboId);
        return ResponseEntity.ok(new APIRespone(true, "Combo deleted successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> addProduct(Long comboId, Long productId) {
        if (comboRepository.findById(comboId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        Combo combo = comboRepository.findById(comboId).get();
        if (combo.getProducts().stream().anyMatch(product -> product.getProductId().equals(productId))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product already exists in combo", ""));
        }
        if (productRepository.findById(productId).isEmpty()){
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        combo.getProducts().add(productRepository.findById(productId).get());
        comboRepository.save(combo);
        return ResponseEntity.ok(new APIRespone(true, "Product added to combo successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> addProducts(Long comboId, List<Long> productIds) {
        if (comboRepository.findById(comboId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        Combo combo = comboRepository.findById(comboId).get();
        for (Long productId : productIds) {
            if (combo.getProducts().stream().anyMatch(product -> product.getProductId().equals(productId))) {
                continue;
            }
            if (productRepository.findById(productId).isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
            }
            combo.getProducts().add(productRepository.findById(productId).get());
        }
        comboRepository.save(combo);
        return ResponseEntity.ok(new APIRespone(true, "Products added to combo successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> getComboById(Long comboId) {
        if (comboRepository.findById(comboId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        List<ComboResponse> comboResponses = comboRepository.findAll().stream()
                .filter(combo1 -> combo1.getComboId().equals(comboId))
                .map(ResponseConverter::convertToComboResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", comboResponses));
    }
}

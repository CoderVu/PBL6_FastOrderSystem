package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.BE_PBL6_FastOrderSystem.repository.SizeRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.SizeResponse;
import com.example.BE_PBL6_FastOrderSystem.service.ISizeService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements ISizeService {
    private final SizeRepository sizeRepository;
    @Override
    public ResponseEntity<APIRespone> getSizeById(Long sizeId) {
        if (sizeRepository.findById(sizeId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Size not found", ""));
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", sizeRepository.findById(sizeId).get()));
    }
    @Override
    public ResponseEntity<APIRespone> getAllSizes() {
        if (sizeRepository.findAll().isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No size found", ""));
        }
        List<SizeResponse> sizeResponses = sizeRepository.findAll().stream()
                .map(size -> new SizeResponse(size.getSizeId(), size.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", sizeResponses));
    }
    
}

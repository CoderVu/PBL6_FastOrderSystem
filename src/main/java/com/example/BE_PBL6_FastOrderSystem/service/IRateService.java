package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.RateRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IRateService {

    ResponseEntity<APIRespone> rateProduct(Long userId, RateRequest rateRequest);

    ResponseEntity<APIRespone> getRateByProduct(Long productId);

    ResponseEntity<APIRespone> getRateByCombo(Long comboId);
}

package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Promotion;
import com.example.BE_PBL6_FastOrderSystem.repository.PromotionRepository;
import com.example.BE_PBL6_FastOrderSystem.response.PromotionResponse;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {

    List<PromotionResponse> getAllPromotion();

    Optional<PromotionResponse> getPromotionById(Long promotionId);
}

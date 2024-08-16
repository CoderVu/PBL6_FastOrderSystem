package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Promotion;
import com.example.BE_PBL6_FastOrderSystem.repository.PromotionRepository;
import com.example.BE_PBL6_FastOrderSystem.response.PromotionResponse;

import java.util.List;

public interface IPromotionService {

    List<PromotionResponse> getAllPromotion();
}

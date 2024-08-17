package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.repository.PromotionRepository;
import com.example.BE_PBL6_FastOrderSystem.response.PromotionResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements IPromotionService {
    private final PromotionRepository promotionRepository;

    @Override
    public List<PromotionResponse> getAllPromotion() {
        return promotionRepository.findAll().stream()
                .map(promotion -> new PromotionResponse(
                        promotion.getId(),
                        promotion.getName(),
                        promotion.getDescription(),
                        promotion.getDiscountPercentage(),
                        promotion.getStartDate(),
                        promotion.getEndDate(),
                        promotion.getStores().stream().map(store -> store.getStoreId()).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PromotionResponse> getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .map(promotion -> new PromotionResponse(
                        promotion.getId(),
                        promotion.getName(),
                        promotion.getDescription(),
                        promotion.getDiscountPercentage(),
                        promotion.getStartDate(),
                        promotion.getEndDate(),
                        promotion.getStores().stream().map(store -> store.getStoreId()).collect(Collectors.toList())
                ));
    }
}
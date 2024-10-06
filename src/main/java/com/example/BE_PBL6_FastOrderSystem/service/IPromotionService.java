package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Promotion;
import com.example.BE_PBL6_FastOrderSystem.repository.PromotionRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.PromotionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {

    ResponseEntity<APIRespone> getAllPromotion();

    ResponseEntity<APIRespone> getPromotionById(Long promotionId);

    ResponseEntity<APIRespone> getAllPromoByStoreId(Long storeId);

    ResponseEntity<APIRespone> addPromotion(PromotionRequest promotionRequest);

    ResponseEntity<APIRespone> applyPromotionsToStore(List<Long> promotionIds, Long storeId);

    ResponseEntity<APIRespone> applyPromotionToStore(Long promotionId, Long storeId);

    ResponseEntity<APIRespone> applyPromotionToAllStores(Long promotionId);

    ResponseEntity<APIRespone> applyPromotionToProduct(Long promotionId, Long productId);


    ResponseEntity<APIRespone> removePromotionsFromStore(List<Long> promotionIds, Long storeId);

    ResponseEntity<APIRespone> applyPromotionToListProducts(Long promotionId, List<Long> productIds);

    ResponseEntity<APIRespone> DeletePromotion(Long promotionId);

    ResponseEntity<APIRespone> updatePromotion(Long promotionId, PromotionRequest promotionRequest);

    ResponseEntity<APIRespone> removePromotionFromProduct(Long promotionId, Long productId);
}
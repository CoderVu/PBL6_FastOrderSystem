package com.example.BE_PBL6_FastOrderSystem.controller.Admin;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {
    @Autowired
    private final IPromotionService promotionService;

    @PostMapping("/add")
    public ResponseEntity<APIRespone> addPromotion(
            @RequestParam("promotionName") String promotionName,
            @RequestParam("discount") Double discount,
            @RequestParam("description") String description,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
            PromotionRequest promotionRequest = new PromotionRequest(promotionName, description, discount, startDate, endDate);
            return promotionService.addPromotion(promotionRequest);
    }
    @PutMapping("/update/{promotionId}")
    public ResponseEntity<APIRespone> updatePromotion(
            @PathVariable Long promotionId,
            @RequestParam("promotionName") String promotionName,
            @RequestParam("discount") Double discount,
            @RequestParam("description") String description,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        PromotionRequest promotionRequest = new PromotionRequest(promotionName, description, discount, startDate, endDate);
        return promotionService.updatePromotion(promotionId, promotionRequest);
    }
    @DeleteMapping("/delete/{promotionId}")
    public ResponseEntity<APIRespone> deletePromotion(@PathVariable Long promotionId) {
        return promotionService.DeletePromotion(promotionId);
    }
    @PutMapping("/apply-to-store")
    public ResponseEntity<APIRespone> applyPromotionToStore(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("storeId") Long storeId) {
         return promotionService.applyPromotionToStore(promotionId, storeId);
    }
    @PutMapping("/apply-to-stores")
    public ResponseEntity<APIRespone> applyPromotionToStore(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("storeIds") List<Long> storeIds) {
        return promotionService.applyPromotionToStores(promotionId, storeIds);
    }
    @PutMapping("/apply-to-all-stores")
    public ResponseEntity<APIRespone>applyPromotionToAllStores(@RequestParam("promotionId") Long promotionId) {
     return promotionService.applyPromotionToAllStores(promotionId);
    }
    @PostMapping("/apply-to-product")
    public ResponseEntity<APIRespone> applyPromotionToProduct(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("productId") Long productId) {
        return promotionService.applyPromotionToProduct(promotionId, productId);
    }
}




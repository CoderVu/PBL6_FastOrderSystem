package com.example.BE_PBL6_FastOrderSystem.controller.Admin;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {
    final IPromotionService promotionService;

    @PostMapping("/add")
    public ResponseEntity<APIRespone> addPromotion(
            @RequestParam("promotionName") String promotionName,
            @RequestParam("discount") Double discount,
            @RequestParam("description") String description,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
            PromotionRequest promotionRequest = new PromotionRequest(promotionName, description, discount, startDate, endDate);
            return promotionService.addPromotion(promotionRequest);

    }
    @PutMapping("store/apply")
    public ResponseEntity<APIRespone> applyPromotionToStore(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("storeId") Long storeId) {
         return promotionService.applyPromotionToStore(promotionId, storeId);
    }
    @PutMapping("/store/apply-to-all")
    public ResponseEntity<APIRespone>applyPromotionToAllStores(@RequestParam("promotionId") Long promotionId) {
     return promotionService.applyPromotionToAllStores(promotionId);
    }
    @PostMapping("/product/apply")
    public ResponseEntity<APIRespone> applyPromotionToProduct(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("productId") Long productId) {
        return promotionService.applyPromotionToProduct(promotionId, productId);
    }
}




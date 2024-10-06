package com.example.BE_PBL6_FastOrderSystem.controller.Admin;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("image") MultipartFile image,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
            PromotionRequest promotionRequest = new PromotionRequest(promotionName, description, image, discount, startDate, endDate);
            return promotionService.addPromotion(promotionRequest);
    }
    @PutMapping("/update/{promotionId}")
    public ResponseEntity<APIRespone> updatePromotion(
            @PathVariable Long promotionId,
            @RequestParam("promotionName") String promotionName,
            @RequestParam("discount") Double discount,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        PromotionRequest promotionRequest = new PromotionRequest(promotionName, description,image, discount, startDate, endDate);
        return promotionService.updatePromotion(promotionId, promotionRequest);
    }
    @DeleteMapping("/delete/{promotionId}")
    public ResponseEntity<APIRespone> deletePromotion(@PathVariable Long promotionId) {
        return promotionService.DeletePromotion(promotionId);
    }
    @PutMapping("/apply-to-store")
    public ResponseEntity<APIRespone> applyPromotionToStore(
            @RequestParam("promotionId") List<Long> promotionIds,
            @RequestParam("storeId") Long storeId) {
         return promotionService.applyPromotionsToStore(promotionIds, storeId);
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
    @PostMapping("/apply-to-list-products")
    public ResponseEntity<APIRespone> applyPromotionToListProducts(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("productIds") List<Long> productIds) {
        return promotionService.applyPromotionToListProducts(promotionId, productIds);
    }
    @PostMapping("/remove-from-store")
    public ResponseEntity<APIRespone> removePromotionsFromStore(
            @RequestParam("promotionIds") List<Long> promotionIds,
            @RequestParam("storeId") Long storeId) {
        return promotionService.removePromotionsFromStore(promotionIds, storeId);
    }
    @PostMapping("/remove-from-product")
    public ResponseEntity<APIRespone> removePromotionFromProduct(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("productId") Long productId) {
        return promotionService.removePromotionFromProduct(promotionId, productId);
    }
}




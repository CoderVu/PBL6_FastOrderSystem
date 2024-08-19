package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.request.ProductRequest;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
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
    public ResponseEntity<?> addPromotion(
            @RequestParam("promotionName") String promotionName,
            @RequestParam("discount") Double discount,
            @RequestParam("description") String description,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            PromotionRequest promotionRequest = new PromotionRequest(promotionName, description, discount, startDate, endDate);
            promotionService.addPromotion(promotionRequest);
            return ResponseEntity.ok().body("Promotion added successfully");
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("store/apply")
    public ResponseEntity<?> applyPromotionToStore(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("storeId") Long storeId) {
        try {
            promotionService.applyPromotionToStore(promotionId, storeId);
            return ResponseEntity.ok().body("Promotion applied successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/store/apply-to-all")
    public ResponseEntity<?> applyPromotionToAllStores(@RequestParam("promotionId") Long promotionId) {
        try {
            promotionService.applyPromotionToAllStores(promotionId);
            return ResponseEntity.ok().body("Promotion applied to all stores successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while applying the promotion");
        }
    }
    @PostMapping("/product/apply")
    public ResponseEntity<?> applyPromotionToProduct(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("productId") Long productId) {
        try {
            promotionService.applyPromotionToProduct(promotionId, productId);
            return ResponseEntity.ok().body("Promotion applied to product successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}




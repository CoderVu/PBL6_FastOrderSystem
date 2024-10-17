package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails.getCurrentUserId;

@RestController
@RequestMapping("/api/v1/owner/promotions")
@RequiredArgsConstructor

public class OwnerPromotionController {
    final IPromotionService promotionService;

    @PostMapping("/product/apply")
    public ResponseEntity<APIRespone> applyPromotionToProduct(
            @RequestParam("promotionId") Long promotionId,
            @RequestParam("productId") Long productId) {
        return promotionService.applyPromotionToProduct(promotionId, productId);
    }
    @GetMapping("/store/{storeId}")
    public ResponseEntity<APIRespone> getAllPromoByStoreId(@PathVariable Long storeId) {
        return promotionService.getAllPromoByStoreId(storeId);
    }

}

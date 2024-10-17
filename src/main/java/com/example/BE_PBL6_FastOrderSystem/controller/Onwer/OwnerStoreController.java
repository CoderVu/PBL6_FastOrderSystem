package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerStoreController {
    private final IStoreService storeService;
    private final FoodUserDetailsService foodUserDetailsService;

    @GetMapping("/stores")
    public ResponseEntity<APIRespone> getOwnerStores() {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        return storeService.getStoreByUserId(ownerId);
    }
}

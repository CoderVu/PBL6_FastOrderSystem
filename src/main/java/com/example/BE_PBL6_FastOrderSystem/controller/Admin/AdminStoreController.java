package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {
    private final IStoreService storeService;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    @PostMapping("/add")
    public ResponseEntity<APIRespone> addStore(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("storeName") String storeName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("latitude") Double latitude,
            @RequestParam("closingTime") LocalDateTime closingTime,
            @RequestParam("longitude") Double longitude,
            @RequestParam("location") String location,
            @RequestParam("openingTime") LocalDateTime openingTime,
            @RequestParam("managerId") Long managerId) {
        StoreRequest storeRequest = new StoreRequest(storeName, image, phoneNumber, location, longitude, latitude, openingTime, closingTime, managerId);
        return storeService.addStore(storeRequest);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<APIRespone> updateStore(
            @PathVariable Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("storeName") String storeName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("latitude") Double latitude,
            @RequestParam("closingTime") LocalDateTime closingTime,
            @RequestParam("longitude") Double longitude,
            @RequestParam("location") String location,
            @RequestParam("openingTime") LocalDateTime openingTime,
            @RequestParam("managerId") Long managerId) {
        StoreRequest storeRequest = new StoreRequest(storeName, image, phoneNumber, location, longitude, latitude, openingTime, closingTime, managerId);
        return storeService.updateStore(id, storeRequest);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<APIRespone> deleteStore(@PathVariable Long id) {
         return storeService.deleteStore(id);
    }
}

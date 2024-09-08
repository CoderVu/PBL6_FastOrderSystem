package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime; 

import java.util.Date;

@RestController
@RequestMapping("/api/v1/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {
    private final IStoreService storeService;
    @PostMapping("/add")
    public ResponseEntity<APIRespone> addStore(
            @RequestParam("storeName") String storeName,
            @RequestParam("image") MultipartFile image,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("location") String location,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("openingTime") LocalDateTime openingTime,
            @RequestParam("closingTime") LocalDateTime closingTime,
            @RequestParam("managerId") Long managerId) {
        StoreRequest storeRequest = new StoreRequest(storeName, image, phoneNumber, location, longitude, latitude, openingTime, closingTime, managerId);
        return storeService.addStore(storeRequest);
    }
    @PutMapping("update/{id}")
    public  ResponseEntity<APIRespone> updateStore(
            @PathVariable Long id,
            @RequestParam("storeName") String storeName,
            @RequestParam("image") MultipartFile image,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("location") String location,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("openingTime") LocalDateTime openingTime,
            @RequestParam("closingTime") LocalDateTime closingTime,
            @RequestParam("managerId") Long managerId) {
        StoreRequest storeRequest = new StoreRequest(storeName, image ,phoneNumber, location, longitude, latitude, openingTime, closingTime, managerId);
        return storeService.updateStore(id, storeRequest);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<APIRespone> deleteStore(@PathVariable Long id) {
         return storeService.deleteStore(id);
    }
}

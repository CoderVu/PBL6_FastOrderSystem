package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.ProductAlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.exception.StoreAlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {
    private final IStoreService storeService;
    @PostMapping("/add")
    public ResponseEntity<?> addStore(
            @RequestParam("storeName") String storeName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("location") String location,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("openingTime") @DateTimeFormat(pattern = "HH:mm:ss") Date openingTime,
            @RequestParam("closingTime") @DateTimeFormat(pattern = "HH:mm:ss") Date closingTime,
            @RequestParam("managerId") Long managerId) {
        try {
            StoreRequest storeRequest = new StoreRequest(storeName, phoneNumber, location, longitude, latitude, openingTime, closingTime, managerId);
            storeService.addStore(storeRequest);
            return ResponseEntity.ok().body("Store added successfully");
        } catch (StoreAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateStore(
            @PathVariable Long id,
            @RequestParam("storeName") String storeName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("location") String location,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("openingTime") @DateTimeFormat(pattern = "HH:mm:ss") Date openingTime,
            @RequestParam("closingTime") @DateTimeFormat(pattern = "HH:mm:ss") Date closingTime,
            @RequestParam("managerId") Long managerId) {
        try {
            StoreRequest storeRequest = new StoreRequest(storeName, phoneNumber, location, longitude, latitude, openingTime, closingTime, managerId);
            storeService.updateStore(id, storeRequest);
            return ResponseEntity.ok().body("Store updated successfully");
        }  catch (StoreAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteStore(@PathVariable Long id) {
        try {
            storeService.deleteStore(id);
            return ResponseEntity.ok().body("Store deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}

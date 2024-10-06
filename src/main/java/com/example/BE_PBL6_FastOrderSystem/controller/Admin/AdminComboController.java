package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.request.ComboRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/combo")
public class AdminComboController {
    private final IComboService comboService;
    @PostMapping("/add")
    ResponseEntity<APIRespone> addCombo(
            @RequestParam("comboName") String comboName,
            @RequestParam("price") Double price,
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description){
        ComboRequest comboRequest = new ComboRequest(comboName, price, image, description);
        return comboService.addCombo(comboRequest);
    }
    @PutMapping("/update/{comboId}")
    ResponseEntity<APIRespone> updateCombo(
            @PathVariable Long comboId,
            @RequestParam("comboName") String comboName,
            @RequestParam("price") Double price,
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description){
        ComboRequest comboRequest = new ComboRequest(comboName, price, image, description);
        return comboService.updateCombo(comboId, comboRequest);
    }
    @DeleteMapping("/delete/{comboId}")
    ResponseEntity<APIRespone> deleteCombo(@PathVariable Long comboId){
        return comboService.deleteCombo(comboId);
    }
    @PostMapping("/addProduct/{comboId}")
    ResponseEntity<APIRespone> addProduct(@PathVariable Long comboId, @RequestParam Long productId){
        return comboService.addProduct(comboId, productId);
    }
    @PostMapping("/addListProduct/{comboId}")
    ResponseEntity<APIRespone> addListProduct(@PathVariable Long comboId, @RequestParam List<Long> productIds){
        return comboService.addProducts(comboId, productIds);
    }



}

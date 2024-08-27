package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.request.ComboRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/combo")
public class AdminComboController {
    private final IComboService comboService;
    @PostMapping("/add")
    ResponseEntity<APIRespone> addCombo(
            @RequestParam("comboName") String comboName,
            @RequestParam("price") Double price,
            @RequestParam("image") MultipartFile image){
        ComboRequest comboRequest = new ComboRequest(comboName, price, image);
        return comboService.addCombo(comboRequest);
    }
    @PutMapping("/update/{comboId}")
    ResponseEntity<APIRespone> updateCombo(
            @PathVariable Long comboId,
            @RequestParam("comboName") String comboName,
            @RequestParam("price") Double price,
            @RequestParam("image") MultipartFile image){
        ComboRequest comboRequest = new ComboRequest(comboName, price, image);
        return comboService.updateCombo(comboId, comboRequest);
    }
    @DeleteMapping("/delete/{comboId}")
    ResponseEntity<APIRespone> deleteCombo(@PathVariable Long comboId){
        return comboService.deleteCombo(comboId);
    }



}

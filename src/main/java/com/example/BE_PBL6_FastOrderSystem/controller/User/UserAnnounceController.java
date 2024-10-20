package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.request.AnnouceRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAnnouceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/announce")
public class UserAnnounceController {
    private final IAnnouceService iAnnouceService;
    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }
    @GetMapping()
    public ResponseEntity<APIRespone> getAllAnnouce() {
        Long userId = getCurrentUserId();
        return iAnnouceService.getbyuserId(userId);
    }
    @PostMapping("/add")
    public ResponseEntity<APIRespone> addAnnouce(@RequestBody AnnouceRequest request) {
        return iAnnouceService.addnewannounce(request.getUserid(), request.getTitle(), request.getContent());
    }
}
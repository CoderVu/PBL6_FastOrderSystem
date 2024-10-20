package com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/momo")
public class PaymentMomoCallbackController {
    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> callBack(@RequestParam Map<String, Object> callbackRequestDTO) {

        Map<String, Object> result = new HashMap<>();
        if (callbackRequestDTO.containsKey("message")
                && callbackRequestDTO.get("message").equals("Success")) {
            result.put("orderId", callbackRequestDTO.get("orderId"));
            result.put("amount", callbackRequestDTO.get("amount"));
            result.put("orderInfo", callbackRequestDTO.get("orderInfo"));
            result.put("message", callbackRequestDTO.get("message"));
        } else {

            result.put("message", callbackRequestDTO.get("message"));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
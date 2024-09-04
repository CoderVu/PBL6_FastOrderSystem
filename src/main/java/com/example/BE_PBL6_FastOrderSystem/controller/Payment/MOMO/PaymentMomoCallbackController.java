package com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.Payment;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
package com.example.BE_PBL6_FastOrderSystem.controller.Payment.ZALOPAY;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/zalopay")
public class PaymentZaloPayCheckStatusController {
	private final IPaymentService paymentService;

	@GetMapping("/get-status")
    public ResponseEntity<APIRespone> getStatus(@RequestParam String apptransid) {
        try {
            // Create a PaymentZaloPayRequest object
            PaymentRequest requestDTO = new PaymentRequest();
            requestDTO.setApptransid(apptransid);
            // Call the service method with the requestDTO
            Map<String, Object> result = this.paymentService.getStatusZaloPay(requestDTO);
            return new ResponseEntity<>(new APIRespone(true, "Order status retrieved successfully", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new APIRespone(false, "Failed to retrieve order status", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
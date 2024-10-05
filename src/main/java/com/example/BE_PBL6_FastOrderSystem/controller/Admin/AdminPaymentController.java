package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments")
public class AdminPaymentController {
    private final IPaymentService paymentService;
    @GetMapping("")
    public ResponseEntity<APIRespone> getAllPayments() {
        return paymentService.getAllPayment();
    }
    @GetMapping("/{paymentId}")
    public ResponseEntity<APIRespone> getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }
    @GetMapping("/payment-detail/{paymentId}")
    public ResponseEntity<APIRespone> getPaymentDetailByPaymentId(@PathVariable Long paymentId) {
        return paymentService.getPaymentDetailByPaymentId(paymentId);
    }

}

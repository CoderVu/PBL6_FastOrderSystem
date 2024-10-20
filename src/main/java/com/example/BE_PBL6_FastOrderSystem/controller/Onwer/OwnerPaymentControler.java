package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/payment")
@RequiredArgsConstructor
public class OwnerPaymentControler {

    private final IOrderService orderService;
    private final PaymentRepository paymentRepository;

    
}
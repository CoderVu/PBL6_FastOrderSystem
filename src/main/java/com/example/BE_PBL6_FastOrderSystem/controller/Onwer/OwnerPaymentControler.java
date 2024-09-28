package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.OrderDetail;
import com.example.BE_PBL6_FastOrderSystem.model.Payment;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/owner/payment")
@RequiredArgsConstructor
public class OwnerPaymentControler {

    private final IOrderService orderService;
    private final PaymentRepository paymentRepository;

    
}
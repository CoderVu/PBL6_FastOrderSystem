package com.example.BE_PBL6_FastOrderSystem.controller.Payment.ZALOPAY;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;

import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/zalopay")
public class PaymentZaloPayCreateOrderController {
	private final IPaymentService paymentService;

@PostMapping("/create-order")
 public ResponseEntity<APIRespone> createOrderZaloPay(@RequestBody PaymentRequest orderRequest) throws IOException, java.io.IOException {
	 Map<String, Object> result = this.paymentService.createOrderZaloPay(orderRequest);
	 return new ResponseEntity<>(new APIRespone(true, "Create order successfully", result), HttpStatus.OK);
 }
}


package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.PaymentMethod;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IPaymentService {
    Map<String, Object> createOrder(PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException;

    Map<String, Object> getStatus(PaymentRequest requestDTO) throws IOException;

    PaymentMethod findPaymentMethodByName(String momo);
    ResponseEntity<APIRespone> savePayment(PaymentRequest orderRequest, Order order, Long userId, String deliveryAddress);
}

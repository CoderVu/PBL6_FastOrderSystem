package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.PaymentMethod;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IPaymentService {
    Map<String, Object> createOrderMomo(PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException;

    ResponseEntity<APIRespone> savePaymentCash(PaymentRequest orderRequest, Order order, Long userId, String deliveryAddress);

    Map<String, Object> createOrderZaloPay(PaymentRequest orderRequest) throws IOException;
    Map<String, Object> getStatusMomo(PaymentRequest requestDTO) throws IOException;
    Map<String, Object> getStatusZaloPay(PaymentRequest requestDTO) throws IOException, URISyntaxException;
    PaymentMethod findPaymentMethodByNameMomo(String momo);
    ResponseEntity<APIRespone> savePaymentMomo(PaymentRequest orderRequest, Order order, Long userId, String deliveryAddress);
    ResponseEntity<APIRespone> savePaymentZaloPay(PaymentRequest orderRequest, Order order, Long userId, String deliveryAddress);
}

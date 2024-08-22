package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.PaymentMethod;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IPaymentService {
    Map<String, Object> createOrder(PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException;

    Map<String, Object> getStatus(PaymentRequest requestDTO) throws IOException;

    PaymentMethod findPaymentMethodByName(String momo);
}

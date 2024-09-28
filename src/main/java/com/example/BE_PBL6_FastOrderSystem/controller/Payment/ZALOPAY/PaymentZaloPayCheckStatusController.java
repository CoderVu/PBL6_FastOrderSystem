package com.example.BE_PBL6_FastOrderSystem.controller.Payment.ZALOPAY;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.BE_PBL6_FastOrderSystem.model.Payment;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.utils.Helper.HelperHmacSHA256;
import com.example.BE_PBL6_FastOrderSystem.utils.constants.ZaloPayConstant;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
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
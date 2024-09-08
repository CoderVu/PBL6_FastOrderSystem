package com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO;

import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.Impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/momo")
public class PaymentMomoCreateOrderController {
    private final IPaymentService paymentService;
    @PostMapping("/momo-payment")
    public ResponseEntity<APIRespone> momoPayment(@RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        Map<String, Object> result = this.paymentService.createOrderMomo(orderRequest);
        return new ResponseEntity<>(new APIRespone(true, "Create order successfully", result), HttpStatus.OK);

    }

}

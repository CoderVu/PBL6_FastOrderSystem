package com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO;

import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.Impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
@RequiredArgsConstructor
@RestController
public class PaymentMomoCheckStatusController {

    private final IPaymentService paymentService;

    @PostMapping("/api/v1/momo/get-status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestBody PaymentRequest requestDTO) throws IOException {

        Map<String, Object> result = this.paymentService.getStatus(requestDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}

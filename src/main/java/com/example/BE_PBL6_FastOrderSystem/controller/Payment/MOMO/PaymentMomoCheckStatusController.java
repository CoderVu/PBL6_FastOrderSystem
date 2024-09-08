package com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO;

import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.Impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/momo")
public class PaymentMomoCheckStatusController {

    private final IPaymentService paymentService;

    @PostMapping("/get-status")
    public ResponseEntity<APIRespone> getStatus(@RequestBody PaymentRequest requestDTO) throws IOException {
        Map<String, Object> result = this.paymentService.getStatusMomo(requestDTO);
        return new ResponseEntity<>(new APIRespone(true, "Get status successfully", result), HttpStatus.OK);

    }

}

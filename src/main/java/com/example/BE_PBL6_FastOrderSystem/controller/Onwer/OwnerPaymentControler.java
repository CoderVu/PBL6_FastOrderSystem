package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.Payment;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/owner/payment")
@RequiredArgsConstructor
public class OwnerPaymentControler {

    private final IOrderService orderService;
    private final PaymentRepository paymentRepository;

    @PutMapping("/update-payment-status/{orderId}")
    public ResponseEntity<APIRespone> updatePaymentStatus(@PathVariable Long orderId) {
        Long ownerId = FoodUserDetails.getCurrentUserId();
        Order order = orderService.findOrderByOrderIdAndOwnerId(orderId, ownerId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Order not found", null));
        }

        Payment payment = paymentRepository.findByOrderCode(order.getOrderCode());
        if (payment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Payment not found", null));
        }

        payment.setStatus("Đã thanh toán");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        return ResponseEntity.ok(new APIRespone(true, "Payment status updated to Đã thanh toán", null));
    } //
}
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

//    @PutMapping("/update-payment-status")
//    public ResponseEntity<APIRespone> updatePaymentStatus(@RequestParam Long OrderId ) {
//        Long ownerId = FoodUserDetails.getCurrentUserId();
//        OrderDetail orderDetail = orderService.findOrderByOrderIdAndOwnerId(OrderId, ownerId).get(0); // get first order detail
//        Order order = orderDetail.getOrder();
//        Payment payment = paymentRepository.findByOrderCode(order.getOrderCode());
//        if (payment == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Payment not found", null));
//        }
//
//        if ("Đã thanh toán".equals(payment.getStatus())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Payment status has already Đã thanh toán", null));
//        }
//
//        payment.setStatus("Đã thanh toán");
//        payment.setPaymentDate(LocalDateTime.now());
//        paymentRepository.save(payment);
//
//        return ResponseEntity.ok(new APIRespone(true, "Payment status updated to Đã thanh toán", null));
//    }
}
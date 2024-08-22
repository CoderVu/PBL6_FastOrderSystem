package com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.Payment;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/momo")
public class PaymentMomoCallbackController {

    private final IOrderService orderService;
    private final IPaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final Map<String, PaymentRequest> orderRequestCache = new HashMap<>();

    @GetMapping("/callback")
    public ResponseEntity<APIRespone> callBack(@RequestParam Map<String, String> callbackRequestDTO) {
        String orderId = callbackRequestDTO.get("orderId");
        String message = callbackRequestDTO.get("message");

        if ("Success".equalsIgnoreCase(message)) {
            // Nhận thông tin đơn hàng từ cache
            PaymentRequest orderRequest = orderRequestCache.get(orderId);
            if (orderRequest != null) {
                // Xử lý đặt hàng
                ResponseEntity<APIRespone> response = orderService.placeOrder(orderRequest.getUserId(), "MOMO", orderRequest.getCartIds(), orderRequest.getDeliveryAddress(), orderRequest.getOrderCode());
                if (response.getStatusCode() == HttpStatus.OK) {
                    // Update order status to "Chưa giao hàng"
                    orderService.updateOrderStatus(orderRequest.getOrderCode(), "Chưa giao hàng");

                    // Retrieve the Order object
                    Order order = orderService.findOrderByOrderCode(orderRequest.getOrderCode());

                    // Tạo và lưu Payment entity
                    Payment payment = new Payment();
                    payment.setOrder(order);
                    payment.setPaymentDate(LocalDateTime.now());
                    payment.setAmountPaid(orderRequest.getAmount().doubleValue());
                    payment.setPaymentMethod(paymentService.findPaymentMethodByName("MOMO"));
                    payment.setStatus("Đã thanh toán trong bảng Payment");
                    payment.setCreatedAt(LocalDateTime.now());
                    payment.setOrderCode(orderRequest.getOrderCode());
                    payment.setUserId(orderRequest.getUserId());
                    payment.setDeliveryAddress(orderRequest.getDeliveryAddress());
                    payment.setOrderInfo(orderRequest.getOrderInfo());
                    payment.setLang(orderRequest.getLang());
                    payment.setExtraData(orderRequest.getExtraData());

                    paymentRepository.save(payment);
                }
                return response;
            } else {
                // Không tìm thấy thông tin đơn hàng
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIRespone(false, "Order information not found", null));
            }
        } else {
            // Thanh toán thất bại
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIRespone(false, "Payment failed: " + message, null));
        }
    }

    // Method to cache order request
    public void cacheOrderRequest(PaymentRequest orderRequest) {
        orderRequestCache.put(orderRequest.getOrderCode(), orderRequest);
    }
}
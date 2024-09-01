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
                ResponseEntity<APIRespone> response = orderService.processProductOrder(orderRequest.getUserId(), "MOMO", orderRequest.getCartIds(), orderRequest.getDeliveryAddress(), orderRequest.getOrderCode());
                if (response.getStatusCode() == HttpStatus.OK) {
                    // Cập nhật trạng thái đơn hàng
                    orderService.updateOrderStatus(orderRequest.getOrderCode(), "Đơn hàng đã được xác nhận");
                    // Nhận thông tin đơn hàng
                    Order order = orderService.findOrderByOrderCode(orderRequest.getOrderCode());
                    // Lưu Payment entity bằng payment service với trạng thái "Đã thanh toán"
                    return paymentService.savePayment(orderRequest, order, orderRequest.getUserId(), orderRequest.getDeliveryAddress());
                } else {
                    // Đặt hàng thất bại
                    return response;
                }
            } else {
                // Không tìm thấy thông tin đơn hàng
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIRespone(false, "Order information not found", null));
            }
        } else {
            // thanh toán thất bại
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIRespone(false, "Payment failed: " + message, null));
        }
    }
    // lưu thông tin đơn hàng vào cache
    public void cacheOrderRequest(PaymentRequest orderRequest) {
        orderRequestCache.put(orderRequest.getOrderCode(), orderRequest);
    }
}
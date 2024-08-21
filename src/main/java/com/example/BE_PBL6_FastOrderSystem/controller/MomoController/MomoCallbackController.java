package com.example.BE_PBL6_FastOrderSystem.controller.MomoController;

import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.CreateOrderPaymentService;
import com.example.BE_PBL6_FastOrderSystem.request.OrderRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/momo")
public class MomoCallbackController {

    private final IOrderService orderService;
    private final CreateOrderPaymentService paymentService;
    private final Map<String, OrderRequestDTO> orderRequestCache = new HashMap<>();

    public MomoCallbackController(IOrderService orderService, CreateOrderPaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/callback")
    public ResponseEntity<APIRespone> callBack(@RequestParam Map<String, String> callbackRequestDTO) {
        String orderId = callbackRequestDTO.get("orderId");
        String message = callbackRequestDTO.get("message");

        if ("Success".equalsIgnoreCase(message)) {
            // Retrieve order information from cache
            OrderRequestDTO orderRequest = orderRequestCache.get(orderId);
            if (orderRequest != null) {
                // Place the order
                return orderService.placeOrder(orderRequest.getUserId(), "MOMO", orderRequest.getCartId(), orderRequest.getDeliveryAddress());
            } else {
                // Order information not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIRespone(false, "Order information not found", null));
            }
        } else {
            // Payment failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIRespone(false, "Payment failed: " + message, null));
        }
    }

    // Thêm phương thức để lưu thông tin đơn hàng vào cache
    public void cacheOrderRequest(OrderRequestDTO orderRequest) {
        orderRequestCache.put(orderRequest.getOrderId(), orderRequest);

    }
}
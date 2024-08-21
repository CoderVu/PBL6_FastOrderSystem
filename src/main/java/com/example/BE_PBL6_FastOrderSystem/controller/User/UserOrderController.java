package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.controller.MomoController.MomoCallbackController;
import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.request.OrderRequestDTO;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.CreateOrderPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class UserOrderController {
    private final IOrderService orderService;
    private final CreateOrderPaymentService paymentService;
    private final MomoCallbackController momoCallbackController;


    @PostMapping("/create")
    public ResponseEntity<APIRespone> placeOrder(
            @RequestParam String paymentMethod,
            @RequestParam Long cartId,
            @RequestParam String deliveryAddress) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        // Check if the cart is empty
        List<CartItem> cartItems = orderService.getCartItemsByCartId(cartId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Cart is empty", ""));
        }

        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            // Tạo ID đơn hàng ngẫu nhiên 6 chữ số
            String orderId = orderService.generateUniqueOrderCode();
            System.out.println("Order ID: " + orderId);

            // Khởi tạo thanh toán MoMo
            OrderRequestDTO orderRequest = new OrderRequestDTO();
            orderRequest.setAmount(calculateOrderAmount(cartId));
            System.out.println("Amount: " + orderRequest.getAmount());
            orderRequest.setOrderId(orderId);
            orderRequest.setCartId(cartId);
            System.out.println("Cart ID: " + orderRequest.getCartId());
            orderRequest.setOrderInfo("Payment for order " + orderId);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");

            // Lưu trữ thông tin đơn hàng vào cache của MoMo callback controller
            momoCallbackController.cacheOrderRequest(orderRequest);

            Map<String, Object> momoResponse = paymentService.createOrder(orderRequest);
            APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
            return ResponseEntity.ok(apiResponse);
        } else {
            // Tiến hành đặt hàng bình thường
            return orderService.placeOrder(paymentMethod, cartId, deliveryAddress);
        }
    }

    private Long calculateOrderAmount(Long cartId) {
        List<CartItem> cartItems = orderService.getCartItemsByCartId(cartId);
        long totalAmount = 0L;
        for (CartItem item : cartItems) {
            totalAmount += item.getTotalPrice();
        }
        return totalAmount;
    }
}

package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.controller.MomoController.MomoCallbackController;
import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.request.OrderRequestDTO;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.CreateOrderPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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
        Long userId = FoodUserDetails.getCurrentUserId();

        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            // Generate random 6-digit order ID
            String orderId = orderService.generateUniqueOrderCode();
            System.out.println("Order ID: " + orderId);
            // Create order request from user input
            OrderRequestDTO orderRequest = new OrderRequestDTO();
            orderRequest.setAmount(calculateOrderAmount(cartId));
            System.out.println("Amount: " + orderRequest.getAmount());
            orderRequest.setOrderId(orderId);
            orderRequest.setCartId(cartId);
            orderRequest.setUserId(userId);
            orderRequest.setDeliveryAddress(deliveryAddress);
            // Check if cartId belongs to the current user
            if (!orderService.getCartItemsByCartId(cartId).get(0).getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Cart does not belong to current user", ""));
            }

            System.out.println("Cart ID: " + orderRequest.getCartId());
            System.out.println("User ID: " + orderRequest.getUserId());
            orderRequest.setOrderInfo("Payment for order " + orderId);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            // Store order information in MoMo callback controller cache
            momoCallbackController.cacheOrderRequest(orderRequest);
            Map<String, Object> momoResponse = paymentService.createOrder(orderRequest);
            APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
            return ResponseEntity.ok(apiResponse);
        } else {
            // Proceed with normal order placement
            return orderService.placeOrder(userId, paymentMethod, cartId, deliveryAddress);
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
    @GetMapping("/history/all")
    public ResponseEntity<APIRespone> getAllOrdersByUser() {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getAllOrdersByUser(userId);
    }
}

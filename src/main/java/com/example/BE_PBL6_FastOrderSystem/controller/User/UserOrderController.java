package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO.MomoCallbackController;
import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.request.OrderRequestDTO;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class UserOrderController {
    private final IOrderService orderService;
    private final CreateOrderPaymentService paymentService;
    private final MomoCallbackController momoCallbackController;

    @PostMapping("/create")
    public ResponseEntity<APIRespone> placeOrder(
            @RequestBody OrderRequestDTO orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        // Extract fields from orderRequest
        String paymentMethod = orderRequest.getPaymentMethod();
        List<Long> cartIds = orderRequest.getCartIds();
        String deliveryAddress = orderRequest.getDeliveryAddress();

        // Check if the carts are empty
        List<CartItem> cartItems = cartIds.stream()
                .flatMap(cartId -> orderService.getCartItemsByCartId(cartId).stream())
                .collect(Collectors.toList());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }
        // Get current user ID
        Long userId = FoodUserDetails.getCurrentUserId();
        // Generate random 6-digit order ID
        String orderCode = orderService.generateUniqueOrderCode();
        orderRequest.setOrderCode(orderCode);
        System.out.println("Order code: " + orderCode);
        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            // Set additional fields in orderRequest
            orderRequest.setAmount(calculateOrderAmount(cartIds));
            System.out.println("Amount: " + orderRequest.getAmount());
            orderRequest.setOrderCode(orderCode);
            orderRequest.setUserId(userId);
            // Check if cartIds belong to the current user
            for (Long cartId : cartIds) {
                if (!orderService.getCartItemsByCartId(cartId).get(0).getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "One or more carts do not belong to current user", ""));
                }
            }

            System.out.println("Cart IDs: " + orderRequest.getCartIds());
            System.out.println("User ID: " + orderRequest.getUserId());
            orderRequest.setOrderInfo("Payment for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            // Store order information in MoMo callback controller cache
            momoCallbackController.cacheOrderRequest(orderRequest);
            Map<String, Object> momoResponse = paymentService.createOrder(orderRequest);
            APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
            return ResponseEntity.ok(apiResponse);
        } else {
            // Proceed with normal order placement
            return orderService.placeOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
        }
    }

    private Long calculateOrderAmount(List<Long> cartIds) {
        List<CartItem> cartItems = cartIds.stream()
                .flatMap(cartId -> orderService.getCartItemsByCartId(cartId).stream())
                .collect(Collectors.toList());
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
    @GetMapping("/status/{orderId}")
    public ResponseEntity<APIRespone> getStatus(@PathVariable Long orderId) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getStatusOrder(orderId, userId);
    }
}
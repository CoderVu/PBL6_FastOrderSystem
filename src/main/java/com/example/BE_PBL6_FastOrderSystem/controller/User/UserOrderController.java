package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO.PaymentMomoCallbackController;
import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class UserOrderController {
    private final IOrderService orderService;
    private final IPaymentService paymentService;
    private final IComboService comboService;
    private final PaymentMomoCallbackController paymentMomoCallbackController;
    private final PaymentRepository paymentRepository;

    @PostMapping("/create/product")
    public ResponseEntity<APIRespone> placeOrder(
            @RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

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

        // Calculate total amount and set it to orderRequest
        Long totalAmount = calculateOrderAmount(cartIds);
        orderRequest.setAmount(totalAmount);
        System.out.println("Amount: " + orderRequest.getAmount());

        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            // Set additional fields in orderRequest
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
            orderRequest.setOrderInfo("Payment MOMO for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            // Store order information in MoMo callback controller cache
            paymentMomoCallbackController.cacheOrderRequest(orderRequest);
            Map<String, Object> momoResponse = paymentService.createOrder(orderRequest);
            APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
            return ResponseEntity.ok(apiResponse);
        } else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            // Proceed with normal order placement
            orderRequest.setOrderCode(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment CASH for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Update order status to "Pending"
                orderService.updateOrderStatus(orderCode, "Chưa giao hàng");
                // Retrieve the Order object
                Order order = orderService.findOrderByOrderCode(orderCode);
                // Create and save Payment entityD
                return paymentService.savePayment(orderRequest, order, userId, deliveryAddress);
            }
            return response;
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Unsupported payment method", ""));
        }
    }
    @PostMapping("/create/combo")
    public ResponseEntity<APIRespone> placeComboOrder(
            @RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String paymentMethod = orderRequest.getPaymentMethod();
        List<Long> cartIds = orderRequest.getCartIds();
        String deliveryAddress = orderRequest.getDeliveryAddress();
        List<CartItem> cartItems = cartIds.stream()
                .flatMap(cartId -> orderService.getCartItemsByCartId(cartId).stream())
                .collect(Collectors.toList());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }
        Long userId = FoodUserDetails.getCurrentUserId();
        String orderCode = orderService.generateUniqueOrderCode();
        orderRequest.setOrderCode(orderCode);
        System.out.println("Order code: " + orderCode);
        Long totalAmount = calculateOrderAmount(cartIds);
        orderRequest.setAmount(totalAmount);
        System.out.println("Amount: " + orderRequest.getAmount());
        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderCode(orderCode);
            orderRequest.setUserId(userId);
            for (Long cartId : cartIds) {
                if (!orderService.getCartItemsByCartId(cartId).get(0).getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "One or more carts do not belong to current user", ""));
                }
            }
            System.out.println("Cart IDs: " + orderRequest.getCartIds());
            System.out.println("User ID: " + orderRequest.getUserId());
            orderRequest.setOrderInfo("Payment MOMO for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            paymentMomoCallbackController.cacheOrderRequest(orderRequest);
            Map<String, Object> momoResponse = paymentService.createOrder(orderRequest);
            APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
            return ResponseEntity.ok(apiResponse);
        } else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderCode(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment CASH for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            ResponseEntity<APIRespone> response = orderService.processComboOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
            if (response.getStatusCode() == HttpStatus.OK) {
                orderService.updateOrderStatus(orderCode, "Chưa giao hàng");
                Order order = orderService.findOrderByOrderCode(orderCode);
                return paymentService.savePayment(orderRequest, order, userId, deliveryAddress);
            }
            return response;
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Unsupported payment method", ""));
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
    @GetMapping("/history/{orderId}")
    public ResponseEntity<APIRespone> getOrderById(@PathVariable Long orderId) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getOrderByIdAndUserId(orderId, userId);
    }
    @GetMapping("/status/{orderId}")
    public ResponseEntity<APIRespone> getStatus(@PathVariable Long orderId) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getStatusOrder(orderId, userId);
    }
}
package com.example.BE_PBL6_FastOrderSystem.controller.User;
import com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO.PaymentMomoCheckStatusController;
import com.example.BE_PBL6_FastOrderSystem.controller.Payment.ZALOPAY.PaymentZaloPayCheckStatusController;
import com.example.BE_PBL6_FastOrderSystem.model.Cart;
import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class UserOrderController {
    private final IOrderService orderService;
    private final IPaymentService paymentService;
    private final IComboService comboService;
    private final PaymentMomoCheckStatusController paymentMomoCheckStatusController;
    private final PaymentZaloPayCheckStatusController paymentZaloPayCheckStatusController;

public ResponseEntity<APIRespone> checkPaymentMomoStatus(PaymentRequest orderRequest) {
    try {
        ResponseEntity<APIRespone> response = paymentMomoCheckStatusController.getStatus(orderRequest);
        System.out.println("Order Request "+ orderRequest);
        Map<String, Object> responseData = (Map<String, Object>) response.getBody().getData();
        System.out.println("reponse Status đã thanh toán hãy chưa:" + responseData);
        if (response.getStatusCode() == HttpStatus.OK && "Success".equals(responseData.get("message"))) {
            return ResponseEntity.ok(new APIRespone(true, "Payment status is successful", responseData));
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Payment status is no pay", ""));
        }
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIRespone(false, "Internal server error", ""));
    }
}
    public ResponseEntity<APIRespone> checkPaymentZaloPayStatus(String apptransid) {
        try {
            // Perform the HTTP request to check payment status
            ResponseEntity<APIRespone> response = paymentZaloPayCheckStatusController.getStatus(apptransid);
            System.out.println("apptransid: " + apptransid);
            System.out.println("Response Status đã thanh toán hãy chưa(zalo): " + response);

            Map<String, Object> responseData = (Map<String, Object>) response.getBody().getData();
            System.out.println("Message response data: " + responseData.get("status"));
            System.out.println("Response get statuscode: " + response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK && "Success".equals(responseData.get("status"))) {

                return ResponseEntity.ok(new APIRespone(true, "Payment status is successful", responseData));
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Payment status check failed", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIRespone(false, "Internal server error", ""));
        }
    }

    @PostMapping("/create/product")
    public ResponseEntity<APIRespone> placeProductOrder(
            @RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String paymentMethod = orderRequest.getPaymentMethod();
        List<Long> cartIds = orderRequest.getCartIds();
        String deliveryAddress = orderRequest.getDeliveryAddress();

        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> orderService.getCartItemsByCartId(cartId).stream())
                .collect(Collectors.toList());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }

        Long userId = FoodUserDetails.getCurrentUserId();
        String orderCode = orderService.generateUniqueOrderCode();
        orderRequest.setOrderId(orderCode);
        orderRequest.setUserId(userId);
        Long totalAmount = calculateOrderAmount(cartIds);
        orderRequest.setAmount(totalAmount);
        System.out.println("Order code: " + orderCode);
        if ("ZALOPAY".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            for (Long cartId : cartIds) {
                if (!orderService.getCartItemsByCartId(cartId).get(0).getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "One or more carts do not belong to current user", ""));
                }
            }
            System.out.println("Cart IDs: " + orderRequest.getCartIds());
            System.out.println("User ID: " + orderRequest.getUserId());
            orderRequest.setOrderInfo("Payment ZaloPay for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            // Initiate ZaloPay payment
            Map<String, Object> zalopayResponse = paymentService.createOrderZaloPay(orderRequest);
            System.out.println("ZaloPay response: " + zalopayResponse);
            System.out.println();
            if (Integer.parseInt(zalopayResponse.get("returncode").toString()) == 1) {
                System.out.println("ZaloPay payment initiation successful");
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Checking payment status...");
                    ResponseEntity<APIRespone> statusResponse = checkPaymentZaloPayStatus(zalopayResponse.get("apptransid").toString());
                    System.out.println("Payment status response: " + statusResponse);

                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("Payment status is OK. Proceeding with order placement...");
                        ResponseEntity<APIRespone> response = orderService.processProductOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
                        System.out.println("Response order khi processProductOrder Zalo: " + response);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            // Step 1: Create a map to store the total quantity for each product in each store
                            Map<Long, Map<Long, Integer>> storeProductQuantities = new HashMap<>();

                            // Step 2: Iterate through the cart items and sum the quantities for each product in each store
                            for (Cart cart : cartItems) {
                                if (cart.getProduct() != null && cart.getProduct().getProductId() != null) {
                                    Long productId = cart.getProduct().getProductId();
                                    Long storeId = cart.getStoreId();
                                    int quantity = cart.getQuantity();

                                    storeProductQuantities
                                            .computeIfAbsent(storeId, k -> new HashMap<>())
                                            .put(productId, storeProductQuantities.get(storeId).getOrDefault(productId, 0) + quantity);
                                } else {
                                    System.err.println("Product or ProductId is null for cartId: " + cart.getCartId());
                                }
                            }

                            // Step 3: Update the product quantities based on the aggregated values
                            for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                                Long storeId = storeEntry.getKey();
                                for (Map.Entry<Long, Integer> productEntry : storeEntry.getValue().entrySet()) {
                                    Long productId = productEntry.getKey();
                                    int totalQuantity = productEntry.getValue();
                                    // Capture the response from the update method
                                    ResponseEntity<APIRespone> updateResponse = orderService.updateQuantityProductOrderByProduct(productId, storeId, totalQuantity);
                                    System.out.println("Updating productId: " + productId + " in storeId: " + storeId + " with total quantity: " + totalQuantity);
                                    // Print the response
                                    System.out.println("Update response: " + updateResponse);
                                }
                            }

                            // Retrieve the Order object
                            Order order = orderService.findOrderByOrderCode(orderCode);
                            // Update order status
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                            // Create and save Payment entity
                            paymentService.savePaymentZaloPay(orderRequest, order, userId, deliveryAddress);
                        }
                        // Cancel the scheduled task
                        scheduler.shutdown();
                    } else {
                        System.out.println("Payment status is not OK. Retrying...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                // Return response with payment URL
                APIRespone apiResponse = new APIRespone(true, "ZaloPay payment initiated", zalopayResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "ZaloPay payment initiation failed", ""));
            }
        }
        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
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
            // initiate MoMo payment
            Map<String, Object> momoResponse = paymentService.createOrderMomo(orderRequest);
            System.out.println("MoMo response khi product: " + momoResponse);
            if ("Success".equals(momoResponse.get("message"))) {
                // schedule a task to check payment status every 10 seconds
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    ResponseEntity<APIRespone> statusResponse = checkPaymentMomoStatus(orderRequest);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("Payment status is OK. Proceeding with order placement...");
                        ResponseEntity<APIRespone> response = orderService.processProductOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
                        System.out.println("Response order khi processProductOrder Zalo: " + response);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            // Step 1: Create a map to store the total quantity for each product in each store
                            Map<Long, Map<Long, Integer>> storeProductQuantities = new HashMap<>();

                            // Step 2: Iterate through the cart items and sum the quantities for each product in each store
                            for (Cart cart : cartItems) {
                                if (cart.getProduct() != null && cart.getProduct().getProductId() != null) {
                                    Long productId = cart.getProduct().getProductId();
                                    Long storeId = cart.getStoreId();
                                    int quantity = cart.getQuantity();

                                    storeProductQuantities
                                            .computeIfAbsent(storeId, k -> new HashMap<>())
                                            .put(productId, storeProductQuantities.get(storeId).getOrDefault(productId, 0) + quantity);
                                } else {
                                    System.err.println("Product or ProductId is null for cartId: " + cart.getCartId());
                                }
                            }

                            // Step 3: Update the product quantities based on the aggregated values
                            for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                                Long storeId = storeEntry.getKey();
                                for (Map.Entry<Long, Integer> productEntry : storeEntry.getValue().entrySet()) {
                                    Long productId = productEntry.getKey();
                                    int totalQuantity = productEntry.getValue();
                                    // Capture the response from the update method
                                    ResponseEntity<APIRespone> updateResponse = orderService.updateQuantityProductOrderByProduct(productId, storeId, totalQuantity);
                                    System.out.println("Updating productId: " + productId + " in storeId: " + storeId + " with total quantity: " + totalQuantity);
                                    // Print the response
                                    System.out.println("Update response: " + updateResponse);
                                }
                            }

                            // Retrieve the Order object
                            Order order = orderService.findOrderByOrderCode(orderCode);
                            // Update order status
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                            // Create and save Payment entity
                            paymentService.savePaymentZaloPay(orderRequest, order, userId, deliveryAddress);
                        }
                        // Cancel the scheduled task
                        scheduler.shutdown();
                    } else {
                        System.out.println("Payment status is not OK. Retrying...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                // return response with payment URL
                APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "MoMo payment initiation failed", ""));
            }
        } 
        
        else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            // proceed with normal order placement
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment CASH for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            ResponseEntity<APIRespone> response = orderService.processProductOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Step 1: Create a map to store the total quantity for each product in each store
                Map<Long, Map<Long, Integer>> storeProductQuantities = new HashMap<>();
                // Step 2: Iterate through the cart items and sum the quantities for each product in each store
                for (Cart cart : cartItems) {
                    if (cart.getProduct() != null && cart.getProduct().getProductId() != null) {
                        Long productId = cart.getProduct().getProductId();
                        Long storeId = cart.getStoreId();
                        int quantity = cart.getQuantity();

                        storeProductQuantities
                                .computeIfAbsent(storeId, k -> new HashMap<>())
                                .put(productId, storeProductQuantities.get(storeId).getOrDefault(productId, 0) + quantity);
                    } else {
                        System.err.println("Product or ProductId is null for cartId: " + cart.getCartId());
                    }
                }
                // Step 3: Update the product quantities based on the aggregated values
                for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                    Long storeId = storeEntry.getKey();
                    for (Map.Entry<Long, Integer> productEntry : storeEntry.getValue().entrySet()) {
                        Long productId = productEntry.getKey();
                        int totalQuantity = productEntry.getValue();
                        System.out.println("Updating productId: " + productId + " in storeId: " + storeId + " with total quantity: " + totalQuantity);
                        orderService.updateQuantityProductOrderByProduct(productId, storeId, totalQuantity);
                    }
                }

                // Retrieve the Order object
                Order order = orderService.findOrderByOrderCode(orderCode);
                // Update order status
                orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                // Create and save Payment entity
                paymentService.savePaymentMomo(orderRequest, order, userId, deliveryAddress);
            }

            return response;
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Unsupported payment method", ""));
        }
    }
    @PostMapping("/create/combo")
    public ResponseEntity<APIRespone> placeComboOrder(@RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String paymentMethod = orderRequest.getPaymentMethod();
        List<Long> cartIds = orderRequest.getCartIds();
        String deliveryAddress = orderRequest.getDeliveryAddress();

        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> orderService.getCartItemsByCartId(cartId).stream())
                .collect(Collectors.toList());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }

        Long userId = FoodUserDetails.getCurrentUserId();
        String orderCode = orderService.generateUniqueOrderCode();
        orderRequest.setOrderId(orderCode);
        orderRequest.setUserId(userId);
        Long totalAmount = calculateOrderAmount(cartIds);
        orderRequest.setAmount(totalAmount);
        System.out.println("Order code: " + orderCode);
        List<Long> comboIds = cartItems.stream()
                .map(cart -> cart.getCombo().getComboId())
                .collect(Collectors.toList());

        System.out.println("Combo IDs: " + comboIds);
        if ("ZALOPAY".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            for (Long cartId : cartIds) {
                if (!orderService.getCartItemsByCartId(cartId).get(0).getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "One or more carts do not belong to current user", ""));
                }
            }
            System.out.println("Cart IDs: " + orderRequest.getCartIds());
            System.out.println("User ID: " + orderRequest.getUserId());
            orderRequest.setOrderInfo("Payment ZaloPay for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            // Initiate ZaloPay payment
            Map<String, Object> zalopayResponse = paymentService.createOrderZaloPay(orderRequest);
            System.out.println("ZaloPay response: " + zalopayResponse);
            System.out.println();
            if (Integer.parseInt(zalopayResponse.get("returncode").toString()) == 1) {
                System.out.println("ZaloPay payment initiation successful");
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Checking payment status...");
                    ResponseEntity<APIRespone> statusResponse = checkPaymentZaloPayStatus(zalopayResponse.get("apptransid").toString());
                    System.out.println("Payment status response: " + statusResponse);

                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("Payment status is OK. Proceeding with order placement...");
                        ResponseEntity<APIRespone> response = orderService.processComboOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
                        System.out.println("Response order khi processProductOrder Zalo: " + response);
                        if (response.getStatusCode()== HttpStatus.OK) {
                            // Step 1: Create a map to store the total quantity for each combo in each store
                            Map<Long, Map<Long, Integer>> storeProductQuantities = new HashMap<>();
                            // Step 2: Iterate through the cart items and sum the quantities for each combo in each store
                            for (Cart cart : cartItems) {
                                if (cart.getCombo() != null && cart.getCombo().getComboId() != null) {
                                    Long comboId = cart.getCombo().getComboId();
                                    Long storeId = cart.getStoreId();
                                    int quantity = cart.getQuantity();
                                    storeProductQuantities
                                            .computeIfAbsent(storeId, k -> new HashMap<>())
                                            .put(comboId, storeProductQuantities.get(storeId).getOrDefault(comboId, 0) + quantity);
                                } else {
                                    System.err.println("Combo or ComboId is null for cartId: " + cart.getCartId());
                                }
                            }
                            // Step 3: Update the combo quantities based on the aggregated values
                            for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                                Long storeId = storeEntry.getKey();
                                for (Map.Entry<Long, Integer> comboEntry : storeEntry.getValue().entrySet()) {
                                    Long comboId = comboEntry.getKey();
                                    int totalQuantity = comboEntry.getValue();
                                    System.out.println("Updating comboId: " + comboId + " in storeId: " + storeId + " with total quantity: " + totalQuantity);
                                    ResponseEntity<APIRespone> updateResponse = orderService.updateQuantityProductOrderByCombo(comboId, storeId, totalQuantity);
                                    System.out.println("Update response khi updatequantity: " + updateResponse);

                                }
                            }
                            // Retrieve the Order object
                            Order order = orderService.findOrderByOrderCode(orderCode);
                            // Update order status
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                            // Create and save Payment entity
                            paymentService.savePaymentZaloPay(orderRequest, order, userId, deliveryAddress);
                        }
                        // Cancel the scheduled task
                        scheduler.shutdown();
                    } else {
                        System.out.println("Payment status is not OK. Retrying...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                // Return response with payment URL
                APIRespone apiResponse = new APIRespone(true, "ZaloPay payment initiated", zalopayResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "ZaloPay payment initiation failed", ""));
            }
        }
        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
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
            // initiate MoMo payment
            Map<String, Object> momoResponse = paymentService.createOrderMomo(orderRequest);
            System.out.println("MoMo response khi product: " + momoResponse);
            if ("Success".equals(momoResponse.get("message"))) {
                // schedule a task to check payment status every 10 seconds
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    ResponseEntity<APIRespone> statusResponse = checkPaymentMomoStatus(orderRequest);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("Payment status is OK. Proceeding with order placement...");
                        ResponseEntity<APIRespone> response = orderService.processComboOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
                        System.out.println("Response order khi processProductOrder Zalo: " + response);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            // Step 1: Create a map to store the total quantity for each combo in each store
                            Map<Long, Map<Long, Integer>> storeProductQuantities = new HashMap<>();
                            // Step 2: Iterate through the cart items and sum the quantities for each combo in each store
                            for (Cart cart : cartItems) {
                                if (cart.getCombo() != null && cart.getCombo().getComboId() != null) {
                                    Long comboId = cart.getCombo().getComboId();
                                    Long storeId = cart.getStoreId();
                                    int quantity = cart.getQuantity();
                                    storeProductQuantities
                                            .computeIfAbsent(storeId, k -> new HashMap<>())
                                            .put(comboId, storeProductQuantities.get(storeId).getOrDefault(comboId, 0) + quantity);
                                } else {
                                    System.err.println("Combo or ComboId is null for cartId: " + cart.getCartId());
                                }
                            }
                            // Step 3: Update the combo quantities based on the aggregated values
                            for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                                Long storeId = storeEntry.getKey();
                                for (Map.Entry<Long, Integer> comboEntry : storeEntry.getValue().entrySet()) {
                                    Long comboId = comboEntry.getKey();
                                    int totalQuantity = comboEntry.getValue();
                                    System.out.println("Updating comboId: " + comboId + " in storeId: " + storeId + " with total quantity: " + totalQuantity);
                                    ResponseEntity<APIRespone> updateResponse = orderService.updateQuantityProductOrderByCombo(comboId, storeId, totalQuantity);
                                    System.out.println("Update response khi updatequantity: " + updateResponse);
                                }
                            }
                            // Retrieve the Order object
                            Order order = orderService.findOrderByOrderCode(orderCode);
                            // Update order status
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                            // Create and save Payment entity
                            paymentService.savePaymentMomo(orderRequest, order, userId, deliveryAddress);
                        }
                        // Cancel the scheduled task
                        scheduler.shutdown();
                    } else {
                        System.out.println("Payment status is not OK. Retrying...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                // return response with payment URL
                APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "MoMo payment initiation failed", ""));
            }
        }

        else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            // proceed with normal order placement
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment CASH for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            ResponseEntity<APIRespone> response = orderService.processComboOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Step 1: Create a map to store the total quantity for each combo in each store
                Map<Long, Map<Long, Integer>> storeProductQuantities = new HashMap<>();
                // Step 2: Iterate through the cart items and sum the quantities for each combo in each store
                for (Cart cart : cartItems) {
                    if (cart.getCombo() != null && cart.getCombo().getComboId() != null) {
                        Long comboId = cart.getCombo().getComboId();
                        Long storeId = cart.getStoreId();
                        int quantity = cart.getQuantity();
                        storeProductQuantities
                                .computeIfAbsent(storeId, k -> new HashMap<>())
                                .put(comboId, storeProductQuantities.get(storeId).getOrDefault(comboId, 0) + quantity);
                    } else {
                        System.err.println("Combo or ComboId is null for cartId: " + cart.getCartId());
                    }
                }
                // Step 3: Update the combo quantities based on the aggregated values
                for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                    Long storeId = storeEntry.getKey();
                    for (Map.Entry<Long, Integer> comboEntry : storeEntry.getValue().entrySet()) {
                        Long comboId = comboEntry.getKey();
                        int totalQuantity = comboEntry.getValue();
                        System.out.println("Updating comboId: " + comboId + " in storeId: " + storeId + " with total quantity: " + totalQuantity);
                        ResponseEntity<APIRespone> updateResponse = orderService.updateQuantityProductOrderByCombo(comboId, storeId, totalQuantity);
                        System.out.println("Update response khi updatequantity: " + updateResponse);
                    }
                }
                // Retrieve the Order object
                Order order = orderService.findOrderByOrderCode(orderCode);
                // Update order status
                orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                // Create and save Payment entity
                paymentService.savePaymentCash(orderRequest, order, userId, deliveryAddress);
            }

            return response;
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Unsupported payment method", ""));
        }


    }

    private Long calculateOrderAmount(List<Long> cartIds) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> orderService.getCartItemsByCartId(cartId).stream())
                .collect(Collectors.toList());
        long totalAmount = 0L;
        for (Cart item : cartItems) {
            totalAmount += item.getTotalPrice();
        }
        return totalAmount;
    }
    @PostMapping("/cancel/{orderCode}")
    public ResponseEntity<APIRespone> cancelOrder(@PathVariable String orderCode) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.cancelOrder(orderCode, userId);
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
    @GetMapping("/history/status")
    public ResponseEntity<APIRespone> getOrdersByStatus(@RequestParam String status) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getOrdersByStatusAndUserId(status, userId);
    }

}
package com.example.BE_PBL6_FastOrderSystem.controller.User;
import com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO.PaymentMomoCheckStatusController;
import com.example.BE_PBL6_FastOrderSystem.controller.Payment.ZALOPAY.PaymentZaloPayCheckStatusController;
import com.example.BE_PBL6_FastOrderSystem.model.Cart;
import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IComboService;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
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
    private final IProductService productService;
    private final PaymentMomoCheckStatusController paymentMomoCheckStatusController;
    private final PaymentZaloPayCheckStatusController paymentZaloPayCheckStatusController;
    private final ProductRepository productRepository;

    public ResponseEntity<APIRespone> checkPaymentMomoStatus(PaymentRequest orderRequest) {
        try {
            ResponseEntity<APIRespone> response = paymentMomoCheckStatusController.getStatus(orderRequest);
            Map<String, Object> responseData = (Map<String, Object>) response.getBody().getData();
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
            Map<String, Object> responseData = (Map<String, Object>) response.getBody().getData();
            if (response.getStatusCode() == HttpStatus.OK && "Success".equals(responseData.get("status"))) {

                return ResponseEntity.ok(new APIRespone(true, "Payment status is successful", responseData));
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Payment status check failed", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIRespone(false, "Internal server error", ""));
        }
    }
    @PostMapping("/create/now")
    public ResponseEntity<APIRespone> placeOrderNow(@RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String paymentMethod = orderRequest.getPaymentMethod();
        Long productId = orderRequest.getProductId();
        Long comboId = orderRequest.getComboId();
        Long drinkId = orderRequest.getDrinkId();
        Long storeId = orderRequest.getStoreId();
        Integer quantity = orderRequest.getQuantity();
        String size = orderRequest.getSize();
        String deliveryAddress = orderRequest.getDeliveryAddress();
        String orderCode = orderService.generateUniqueOrderCode();
        Long userId = FoodUserDetails.getCurrentUserId();
        orderRequest.setOrderId(orderCode);
        orderRequest.setUserId(userId);
        orderRequest.setAmount(orderService.calculateOrderNowAmount(productId, comboId, quantity));
        if ("ZALOPAY".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
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
                        ResponseEntity<APIRespone> response = orderService.processOrderNow(userId, paymentMethod, productId, comboId, drinkId ,storeId, quantity, size, deliveryAddress, orderCode);
                       System.out.println("Response khi processOrderNow = ZALOPAY: " + response);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            System.out.println(("vo luon"));
                            orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
                            ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                            OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                            paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
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
                        ResponseEntity<APIRespone> response = orderService.processOrderNow(userId, paymentMethod, productId, comboId, drinkId ,storeId, quantity, size, deliveryAddress, orderCode);
                        if (response.getStatusCode() == HttpStatus.OK) {
        
                            orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
                            ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                            OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                            paymentService.savePayment(orderRequest, data.getOrderId(), userId);
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
            ResponseEntity<APIRespone> response = orderService.processOrderNow(userId, paymentMethod, productId, comboId, drinkId ,storeId, quantity, size, deliveryAddress, orderCode);
            System.out.println("Response order khi processOrderNow = CASH: " + response);
            if (response.getStatusCode() == HttpStatus.OK) {
                orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
                ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
                paymentService.savePayment(orderRequest, data.getOrderId(), userId);
            }
            return response;
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Unsupported payment method", ""));
        }
    }
    @PostMapping("/create")
    public ResponseEntity<APIRespone> placeOrder(@RequestBody PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
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
        if ("ZALOPAY".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            for (Long cartId : cartIds) {
                if (!orderService.getCartItemsByCartId(cartId).get(0).getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "One or more carts do not belong to current user", ""));
                }
            }
            orderRequest.setOrderInfo("Payment ZaloPay for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            // Initiate ZaloPay payment
            Map<String, Object> zalopayResponse = paymentService.createOrderZaloPay(orderRequest);
            if (Integer.parseInt(zalopayResponse.get("returncode").toString()) == 1) {
                System.out.println("ZaloPay payment initiation successful");
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    ResponseEntity<APIRespone> statusResponse = checkPaymentZaloPayStatus(zalopayResponse.get("apptransid").toString());
                    System.out.println("Payment status response: " + statusResponse);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
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
                            // Step 3: Update the product quantities and combo quantities based on the aggregated values
                            for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                                Long storeId = storeEntry.getKey();
                                for (Map.Entry<Long, Integer> productEntry : storeEntry.getValue().entrySet()) {
                                    Long productId = productEntry.getKey();
                                    int totalQuantity = productEntry.getValue();
                                    orderService.updateQuantityProductOrderByProduct(productId, storeId, totalQuantity);
                                }
                            }
                            for (Map.Entry<Long, Map<Long, Integer>> storeEntry : storeProductQuantities.entrySet()) {
                                Long storeId = storeEntry.getKey();
                                for (Map.Entry<Long, Integer> comboEntry : storeEntry.getValue().entrySet()) {
                                    Long comboId = comboEntry.getKey();
                                    int totalQuantity = comboEntry.getValue();
                                    orderService.updateQuantityProductOrderByCombo(comboId, storeId, totalQuantity);
                                }
                            }
                        
                           
                            ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                            OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                            paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
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
            if ("Success".equals(momoResponse.get("message"))) {
                // schedule a task to check payment status every 10 seconds
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    ResponseEntity<APIRespone> statusResponse = checkPaymentMomoStatus(orderRequest);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
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
                                    orderService.updateQuantityProductOrderByProduct(productId, storeId, totalQuantity);
                                }
                            }

                            ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                            OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                            paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                            orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
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
            ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress, orderCode);
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
                        orderService.updateQuantityProductOrderByProduct(productId, storeId, totalQuantity);
                    }
                }
                ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
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
    public ResponseEntity<APIRespone> getAllOrders() {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getAllOrderDetailsByUser(userId);
    }
    @GetMapping("/history/{orderCode}")
    public ResponseEntity<APIRespone> findOrderByOrderIdAndUserId(@PathVariable String orderCode) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.findOrderByOrderCodeAndUserId(orderCode, userId);
    }

    @GetMapping("/history/status")
    public ResponseEntity<APIRespone> getOrdersByStatus(@RequestParam String status) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.getOrdersByStatusAndUserId(status, userId);
    }
    @GetMapping("/find")
    public ResponseEntity<APIRespone> findOrderByOrderCode(@RequestParam String orderCode) {
        return orderService.findOrderByOrderCode(orderCode);
    }


}
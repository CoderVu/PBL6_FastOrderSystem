package com.example.BE_PBL6_FastOrderSystem.controller.User;
import com.example.BE_PBL6_FastOrderSystem.controller.Payment.MOMO.PaymentMomoCheckStatusController;
import com.example.BE_PBL6_FastOrderSystem.controller.Payment.ZALOPAY.PaymentZaloPayCheckStatusController;
import com.example.BE_PBL6_FastOrderSystem.entity.Cart;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    private final PaymentMomoCheckStatusController paymentMomoCheckStatusController;
    private final PaymentZaloPayCheckStatusController paymentZaloPayCheckStatusController;


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
        List<Long> drinkId = orderRequest.getDrinkId();
        Long storeId = orderRequest.getStoreId();
        Integer quantity = orderRequest.getQuantity();
        String size = orderRequest.getSize();
        String deliveryAddress = orderRequest.getDeliveryAddress();
        Double longitude = orderRequest.getLongitude();
        Double latitude = orderRequest.getLatitude();
        String orderCode = orderService.generateUniqueOrderCode();
        Long userId = FoodUserDetails.getCurrentUserId();
        String discountCode = orderRequest.getDiscountCode();
        orderRequest.setOrderId(orderCode);
        orderRequest.setUserId(userId);
        orderRequest.setAmount(orderService.calculateOrderNowAmount(productId, comboId, quantity, storeId, latitude, longitude, discountCode, size));
        if ("ZALOPAY".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            Map<String, Object> zalopayResponse = paymentService.createOrderZaloPay(orderRequest);
            if (Integer.parseInt(zalopayResponse.get("returncode").toString()) == 1) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final int[] count = {0};
                scheduler.scheduleAtFixedRate(() -> {
                    count[0]++;
                    if (count[0] > 10) {
                        scheduler.shutdown();
                        return;
                    }
                    ResponseEntity<APIRespone> statusResponse = checkPaymentZaloPayStatus(zalopayResponse.get("apptransid").toString());
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        ResponseEntity<APIRespone> response = orderService.processOrderNow(userId, paymentMethod, productId, comboId, drinkId ,storeId, quantity, size, deliveryAddress, longitude,latitude ,orderCode, discountCode);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
                            ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                            OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                            paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                        }
                        scheduler.shutdown();
                    } else {
                        System.out.println("Waiting for payment status...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                APIRespone apiResponse = new APIRespone(true, "ZaloPay payment initiated", zalopayResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "ZaloPay payment initiation failed", ""));
            }
        }
        if ("MOMO".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            Map<String, Object> momoResponse = paymentService.createOrderMomo(orderRequest);
            if ("Success".equals(momoResponse.get("message"))) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final int[] count = {0};
                scheduler.scheduleAtFixedRate(() -> {
                    count[0]++;
                    if (count[0] > 10) {
                        scheduler.shutdown();
                        return;
                    }
                    ResponseEntity<APIRespone> statusResponse = checkPaymentMomoStatus(orderRequest);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        ResponseEntity<APIRespone> response = orderService.processOrderNow(userId, paymentMethod, productId, comboId, drinkId ,storeId, quantity, size, deliveryAddress, longitude,latitude, orderCode, discountCode);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
                            ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                            OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                            paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                        }
                        scheduler.shutdown();
                    } else {
                        System.out.println("Waiting for payment status...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "MoMo payment initiation failed", ""));
            }
        }
        else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment CASH for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            System.out.println("Check 1 " + orderRequest);
            ResponseEntity<APIRespone> response = orderService.processOrderNow(userId, paymentMethod, productId, comboId, drinkId ,storeId, quantity, size, deliveryAddress,  longitude,latitude, orderCode, discountCode);
            if (response.getStatusCode() == HttpStatus.OK) {
                orderService.updateQuantityProduct(productId, comboId, storeId, quantity);
                ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
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
        Double longitude = orderRequest.getLongitude();
        Double latitude = orderRequest.getLatitude();

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
        String discountCode = orderRequest.getDiscountCode();
        long totalAmount = orderService.calculateOrderAmount(cartIds, latitude, longitude, discountCode);
        System.out.println("Total amounsst: " + totalAmount);
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
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final int[] count = {0};
                scheduler.scheduleAtFixedRate(() -> {
                    count[0]++;
                    if (count[0] > 10) {
                        scheduler.shutdown();
                        return;
                    }
                    ResponseEntity<APIRespone> statusResponse = checkPaymentZaloPayStatus(zalopayResponse.get("apptransid").toString());
                    System.out.println("Payment status response: " + statusResponse);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress,longitude,latitude, orderCode,discountCode);
                        System.out.println("Check zalo " + response);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            // duyệt qua tất cả các giỏ hàng
                            for (Cart cart : cartItems) {
                                Long storeId = cart.getStoreId();
                                int quantity = cart.getQuantity();
                                if (cart.getProduct() != null) {
                                    Long productId = cart.getProduct().getProductId();
                                    orderService.updateQuantityProduct(productId, null, storeId, quantity);
                                }
                                if (cart.getCombo() != null) {
                                    Long comboId = cart.getCombo().getComboId();
                                    orderService.updateQuantityProduct(null, comboId, storeId, quantity);
                                }
                            }
                        }
                        ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                        OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                        System.out.println("data: " + data);
                        paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                    //   orderService.updateOrderStatus(orderCode, "Đơn hàng đã được xác nhận");
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
            orderRequest.setOrderInfo("Payment MOMO for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            Map<String, Object> momoResponse = paymentService.createOrderMomo(orderRequest);
            if ("Success".equals(momoResponse.get("message"))) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final int[] count = {0};
                scheduler.scheduleAtFixedRate(() -> {
                    count[0]++;
                    if (count[0] > 10) {
                        scheduler.shutdown();
                        return;
                    }
                    ResponseEntity<APIRespone> statusResponse = checkPaymentMomoStatus(orderRequest);
                    if (statusResponse.getStatusCode() == HttpStatus.OK) {
                        ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress,  longitude,latitude, orderCode, discountCode);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            for (Cart cart : cartItems) {
                                Long storeId = cart.getStoreId();
                                int quantity = cart.getQuantity();
                                if (cart.getProduct() != null) {
                                    Long productId = cart.getProduct().getProductId();
                                    orderService.updateQuantityProduct(productId, null, storeId, quantity);
                                }

                                if (cart.getCombo() != null) {
                                    Long comboId = cart.getCombo().getComboId();
                                    orderService.updateQuantityProduct(null, comboId, storeId, quantity);
                                }
                            }
                        }
                        ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                        OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                        paymentService.savePayment(orderRequest, data.getOrderId(), userId);
                        scheduler.shutdown();
                    } else {
                        System.out.println("Waiting for payment status...");
                    }
                }, 0, 10, TimeUnit.SECONDS);
                APIRespone apiResponse = new APIRespone(true, "MoMo payment initiated", momoResponse);
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "MoMo payment initiation failed", ""));
            }
        }

        else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            orderRequest.setOrderId(orderCode);
            orderRequest.setUserId(userId);
            orderRequest.setOrderInfo("Payment CASH for order " + orderCode);
            orderRequest.setLang("en");
            orderRequest.setExtraData("additional data");
            ResponseEntity<APIRespone> response = orderService.processOrder(userId, paymentMethod, cartIds, deliveryAddress,  longitude,latitude, orderCode, discountCode);
            System.out.println("Check 1 " + response);
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.getStatusCode() == HttpStatus.OK) {
                    for (Cart cart : cartItems) {
                        Long storeId = cart.getStoreId();
                        int quantity = cart.getQuantity();
                        if (cart.getProduct() != null) {
                            Long productId = cart.getProduct().getProductId();
                            orderService.updateQuantityProduct(productId, null, storeId, quantity);
                        }
                        if (cart.getCombo() != null) {
                            Long comboId = cart.getCombo().getComboId();
                            orderService.updateQuantityProduct(null, comboId, storeId, quantity);
                        }
                    }
                }
                ResponseEntity<APIRespone> orderResponse = orderService.findOrderByOrderCode(orderCode);
                OrderResponse data = (OrderResponse) orderResponse.getBody().getData();
                paymentService.savePayment(orderRequest, data.getOrderId(), userId);
            }
            return response;
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Unsupported payment method", ""));
        }
    }

    @PostMapping("/cancel/{orderCode}")
    public ResponseEntity<APIRespone> cancelOrder(@PathVariable String orderCode) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return orderService.cancelOrder(orderCode, userId);
    }
//    @GetMapping("/history")
//    public ResponseEntity<APIRespone> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "1") int size) {
//        Long userId = FoodUserDetails.getCurrentUserId();
//        return orderService.getAllOrderDetailsByUser(userId, page, size);
//    }
@GetMapping("/history")
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
    @PostMapping("/refund/payment/zalo")
    public ResponseEntity<APIRespone> refundPaymentZaloPay(@RequestBody PaymentRequest request) throws IOException, URISyntaxException {
        return paymentService.refundPaymentZaloPay(request);
    }
    @PostMapping("/update/feedback/{orderid}")
    public ResponseEntity<APIRespone> updateFeedback(@PathVariable Long orderid) {
        return  orderService.updateStatusFeedBack(orderid);
    }


}
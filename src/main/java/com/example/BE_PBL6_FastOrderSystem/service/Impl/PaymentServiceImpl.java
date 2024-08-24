package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.Payment;
import com.example.BE_PBL6_FastOrderSystem.model.PaymentMethod;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentMethodRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.utils.Helper.MoMoHelper;
import com.example.BE_PBL6_FastOrderSystem.utils.constants.MoMoConstant;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements IPaymentService {
      private final PaymentMethodRepository paymentMethodRepository;
      private final PaymentRepository paymentRepository;
      @Override
      public Map<String, Object> createOrder(PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        JSONObject json = new JSONObject();
        json.put("partnerCode", MoMoConstant.PARTNER_CODE);
        json.put("accessKey", MoMoConstant.ACCESS_KEY);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("amount", orderRequest.getAmount().toString());
        json.put("orderId", orderRequest.getOrderCode());
        json.put("cartId", orderRequest.getCartIds());
        json.put("orderInfo", "Thanh toan don hang " + orderRequest.getOrderCode());
        json.put("returnUrl", MoMoConstant.REDIRECT_URL);
        json.put("notifyUrl", MoMoConstant.NOTIFY_URL);
        json.put("requestType", MoMoConstant.REQUEST_TYPE);

        String data = "partnerCode=" + MoMoConstant.PARTNER_CODE
                + "&accessKey=" + MoMoConstant.ACCESS_KEY
                + "&requestId=" + json.get("requestId")
                + "&amount=" + json.get("amount")
                + "&orderId=" + json.get("orderId")
                + "&orderInfo=" + json.get("orderInfo")
                + "&returnUrl=" + MoMoConstant.REDIRECT_URL
                + "&notifyUrl=" + MoMoConstant.NOTIFY_URL
                + "&extraData=";

        String signatureKey = MoMoHelper.computeHmacSha256(data, MoMoConstant.SECRET_KEY);
        json.put("signature", signatureKey);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(MoMoConstant.CREATE_ORDER_URL);

        StringEntity stringEntity = new StringEntity(json.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(stringEntity);

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject object = new JSONObject(resultJsonStr.toString());
        Map<String, Object> result = new HashMap<>();
        for (Iterator<String> it = object.keys(); it.hasNext(); ) {
            String key = it.next();
            result.put(key, object.get(key));
        }

        return result;
    }
    @Override
    public Map<String, Object> getStatus(PaymentRequest requestDTO) throws IOException {

        JSONObject json = new JSONObject();
        json.put("partnerCode", MoMoConstant.PARTNER_CODE);
        json.put("accessKey", MoMoConstant.ACCESS_KEY);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("orderId", requestDTO.getOrderCode());
        //json.put("cardId", requestDTO.getCartIds());
        json.put("requestType", MoMoConstant.CHECK_STATUS_TYPE);

        String data = "partnerCode=" + MoMoConstant.PARTNER_CODE
                + "&accessKey=" + json.get("accessKey")
                + "&requestId=" + json.get("requestId")
                + "&orderId=" + json.get("orderId")
                //  + "&cardId=" + json.get("cardId")
                + "&requestType=" + json.get("requestType");
        String signatureKey = MoMoHelper.computeHmacSha256(data, MoMoConstant.SECRET_KEY);
        json.put("signature", signatureKey);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(MoMoConstant.CREATE_ORDER_URL);
        StringEntity stringEntity = new StringEntity(json.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(stringEntity);
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {

            resultJsonStr.append(line);
        }

        JSONObject object = new JSONObject(resultJsonStr.toString());
        Map<String, Object> result = new HashMap<>();
        for (Iterator<String> it = object.keys(); it.hasNext(); ) {

            String key = it.next();
            result.put(key, object.get(key));
        }
        return result;
    }
    @Override
    public PaymentMethod findPaymentMethodByName(String momo) {
        return paymentMethodRepository.findByName(momo).orElseThrow(() -> new RuntimeException("Payment method not found"));
    }


    @Override
    public ResponseEntity<APIRespone> savePayment(PaymentRequest orderRequest, Order order, Long userId, String deliveryAddress) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmountPaid(orderRequest.getAmount().doubleValue());
        payment.setPaymentMethod(findPaymentMethodByName(orderRequest.getPaymentMethod()));
        payment.setStatus(orderRequest.getPaymentMethod().equalsIgnoreCase("MOMO") ? "Đã thanh toán" : "Chưa thanh toán");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setOrderCode(orderRequest.getOrderCode());
        payment.setUserId(userId);
        payment.setDeliveryAddress(deliveryAddress);
        payment.setOrderInfo(orderRequest.getOrderInfo());
        payment.setLang(orderRequest.getLang());
        payment.setExtraData(orderRequest.getExtraData());
        paymentRepository.save(payment);
        return ResponseEntity.ok(new APIRespone(true, "Payment saved successfully", ""));
    }
}

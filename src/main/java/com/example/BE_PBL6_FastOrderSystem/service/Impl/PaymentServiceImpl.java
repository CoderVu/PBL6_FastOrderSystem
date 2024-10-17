package com.example.BE_PBL6_FastOrderSystem.service.Impl;
import com.example.BE_PBL6_FastOrderSystem.entity.*;
import com.example.BE_PBL6_FastOrderSystem.repository.OrderDetailRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.OrderRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentDetailRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentMethodRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PaymentRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.PaymentDetailResponse;
import com.example.BE_PBL6_FastOrderSystem.response.PaymentResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IPaymentService;
import com.example.BE_PBL6_FastOrderSystem.utils.Helper.HelperHmacSHA256;
import com.example.BE_PBL6_FastOrderSystem.utils.constants.MoMoConstant;
import com.example.BE_PBL6_FastOrderSystem.utils.constants.ZaloPayConstant;
import lombok.RequiredArgsConstructor;
import java.util.*;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements IPaymentService {
      private final PaymentMethodRepository paymentMethodRepository;
      private final PaymentRepository paymentRepository;
      private final PaymentDetailRepository paymentDetailRepository;
      private final OrderRepository orderRepository;
      private final OrderDetailRepository orderDetailRepository;
      private static final Logger logger = Logger.getLogger(PaymentServiceImpl.class.getName());
      public static String getCurrentTimeString(String format) {
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
            SimpleDateFormat fmt = new SimpleDateFormat(format);
            fmt.setCalendar(cal);
            return fmt.format(cal.getTimeInMillis());
        }
      @Override
      public Map<String, Object> createOrderMomo(PaymentRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        JSONObject json = new JSONObject();
        json.put("partnerCode", MoMoConstant.PARTNER_CODE);
        json.put("accessKey", MoMoConstant.ACCESS_KEY);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("amount", orderRequest.getAmount().toString());
        json.put("orderId", orderRequest.getOrderId());
        json.put("cartId", orderRequest.getCartIds());
        json.put("orderInfo", "Thanh toan don hang " + orderRequest.getOrderId());
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

        String signatureKey = HelperHmacSHA256.computeHmacSha256(data, MoMoConstant.SECRET_KEY);
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
    public Map<String, Object> getStatusMomo(PaymentRequest requestDTO) throws IOException {

        JSONObject json = new JSONObject();
        json.put("partnerCode", MoMoConstant.PARTNER_CODE);
        json.put("accessKey", MoMoConstant.ACCESS_KEY);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("orderId", requestDTO.getOrderId());
        //json.put("cardId", requestDTO.getCartIds());
        json.put("requestType", MoMoConstant.CHECK_STATUS_TYPE);

        String data = "partnerCode=" + MoMoConstant.PARTNER_CODE
                + "&accessKey=" + json.get("accessKey")
                + "&requestId=" + json.get("requestId")
                + "&orderId=" + json.get("orderId")
                //  + "&cardId=" + json.get("cardId")
                + "&requestType=" + json.get("requestType");
        String signatureKey = HelperHmacSHA256.computeHmacSha256(data, MoMoConstant.SECRET_KEY);
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
    @Transactional
    @Override
    public ResponseEntity<APIRespone> savePayment(PaymentRequest orderRequest, Long orderId, Long userId) {
        Optional<Order> optionalOrder = orderRepository.findByOrderId(orderId);
        if (!optionalOrder.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Order not found", ""));
        }

        Order order = optionalOrder.get();
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmountPaid(orderRequest.getAmount().doubleValue());
        payment.setPaymentMethod(paymentMethodRepository.findByName(orderRequest.getPaymentMethod()));
        payment.setStatus(orderRequest.getPaymentMethod().equalsIgnoreCase("MOMO") || orderRequest.getPaymentMethod().equalsIgnoreCase("ZALOPAY") ? "Đã thanh toán" : "Chưa thanh toán");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setOrderCode(orderRequest.getOrderId());
        payment.setUserId(userId);
        payment.setOrderInfo(orderRequest.getOrderInfo());
        payment.setLang(orderRequest.getLang());
        payment.setExtraData(orderRequest.getExtraData());
        paymentRepository.save(payment);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getOrderId());

        // nhom cac OrderDetail theo Store
        Map<Store, List<OrderDetail>> groupedOrderDetails = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getStore));

        for (Map.Entry<Store, List<OrderDetail>> entry : groupedOrderDetails.entrySet()) {
            Store store = entry.getKey();
            List<OrderDetail> orderDetailList = entry.getValue();

            // tinh tong tien cua cac OrderDetail cua Store
            double totalAmount = orderDetailList.stream().mapToDouble(OrderDetail::getTotalPrice).sum();

            PaymentDetail paymentDetail = new PaymentDetail();
            paymentDetail.setPayment(payment);
            paymentDetail.setOrder(order);
            paymentDetail.setStore(store);
            paymentDetail.setTotalAmount(totalAmount);
            paymentDetail.setPaymentStatus(orderRequest.getPaymentMethod().equalsIgnoreCase("MOMO") || orderRequest.getPaymentMethod().equalsIgnoreCase("ZALOPAY") ? "Đã thanh toán" : "Chưa thanh toán");
            paymentDetail.setCreatedAt(LocalDateTime.now());
            paymentDetail.setUpdatedAt(LocalDateTime.now());
            paymentDetailRepository.save(paymentDetail);

            System.out.println("Payment detail saved for store: " + store.getStoreName());
        }
        return ResponseEntity.ok(new APIRespone(true, "Payment and payment details saved successfully", ""));
    }

        @Override
        public Map<String, Object> createOrderZaloPay(PaymentRequest orderRequest) throws IOException {
        Long amount = orderRequest.getAmount();
        String order_id = orderRequest.getOrderId();
        String apptransid = getCurrentTimeString("yyMMdd") + "_" + new Date().getTime();
        Map<String, Object> zalopay_Params = new HashMap<>();
        zalopay_Params.put("appid", ZaloPayConstant.APP_ID);
        zalopay_Params.put("apptransid", apptransid);
        zalopay_Params.put("apptime", System.currentTimeMillis());
        zalopay_Params.put("appuser", ZaloPayConstant.APP_ID);
        zalopay_Params.put("amount", amount);
        zalopay_Params.put("description", "Thanh toan don hang #" + order_id);
        zalopay_Params.put("bankcode", "");
        String item = "Thanh toan don hang #" + order_id;
        zalopay_Params.put("item", item);
        Map<String, String> embeddata = new HashMap<>();
        embeddata.put("merchantinfo", "eshop123");
        embeddata.put("promotioninfo", "");
        embeddata.put("redirecturl", ZaloPayConstant.REDIRECT_URL);

        Map<String, String> columninfo = new HashMap<>();
        columninfo.put("store_name", "E-Shop");
        embeddata.put("columninfo", new JSONObject(columninfo).toString());
        zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

        String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
                + zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
                + zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
                + zalopay_Params.get("item");
        zalopay_Params.put("mac", HelperHmacSHA256.computeHmacSha256(data, ZaloPayConstant.KEY1));
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConstant.CREATE_ORDER_URL);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
            if (e.getValue() != null) {
                params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
            } else {
                System.err.println("Null value for key: " + e.getKey());
            }
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<>();
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("orderurl", result.get("orderurl"));
        kq.put("returncode", result.get("returncode"));
        kq.put("zptranstoken", result.get("zptranstoken"));
        kq.put("order_id", order_id); // Ensure order_id is included in the response
        kq.put("apptransid", apptransid); // Ensure apptransid is included in the response
        return kq;
    }
    @Override
    public Map<String, Object> getStatusZaloPay(PaymentRequest requestDTO) throws IOException, URISyntaxException {
      String appid = ZaloPayConstant.APP_ID;
		String key1 = ZaloPayConstant.KEY1;
		String data = appid + "|" + requestDTO.getApptransid() + "|" + key1;
		String mac = HelperHmacSHA256.computeHmacSha256(data, ZaloPayConstant.KEY1);
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("appid", appid));
		params.add(new BasicNameValuePair("apptransid", requestDTO.getApptransid()));
		params.add(new BasicNameValuePair("mac", mac));

		URIBuilder uri = new URIBuilder(ZaloPayConstant.GET_STATUS_PAY_URL);
		uri.addParameters(params);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(uri.build());

		CloseableHttpResponse res = client.execute(get);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		StringBuilder resultJsonStr = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			resultJsonStr.append(line);
		}
		JSONObject result = new JSONObject(resultJsonStr.toString());
		Map<String, Object> response = new HashMap<>();
		// Extract relevant fields from the response
		int returncode = result.getInt("returncode");
		boolean isProcessing = result.getBoolean("isprocessing");
		long amount = result.getLong("amount");
		long discountAmount = result.optLong("discountamount", 0);
		String zptransid = result.optString("zptransid", "");

		response.put("returncode", returncode);
		response.put("returnmessage", result.getString("returnmessage"));
		response.put("isprocessing", isProcessing);
		response.put("amount", amount);
		response.put("discountamount", discountAmount);
		response.put("zptransid", zptransid);
		// Check if the order has been paid or not
		if (returncode == 1 && !isProcessing) {
			response.put("status", "Success");
			response.put("message", "The payment has been completed successfully.");
		} else if (isProcessing) {
			response.put("status", "Processing");
			response.put("message", "The payment is still processing.");
		} else {
			response.put("status", "Failed");
			response.put("message", "The payment has not been completed.");
		}

		return response;
	}
    @Override
    public ResponseEntity<APIRespone> refundPaymentZaloPay(PaymentRequest requestDTO) throws IOException, URISyntaxException {
        String appid = ZaloPayConstant.APP_ID;
        String key1 = ZaloPayConstant.KEY1;
        String data = appid + "|" + requestDTO.getApptransid() + "|" + key1;
        String mac = HelperHmacSHA256.computeHmacSha256(data, ZaloPayConstant.KEY1);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", appid));
        params.add(new BasicNameValuePair("apptransid", requestDTO.getApptransid()));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ZaloPayConstant.REFUND_URL);
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> response = new HashMap<>();
        response.put("returncode", result.getInt("returncode"));
        response.put("returnmessage", result.getString("returnmessage"));
        return ResponseEntity.ok(new APIRespone(true, "Refund successfully", response));
    }
    @Override
    public ResponseEntity<APIRespone> getAllPayment(){
          if (paymentRepository.findAll().isEmpty()) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "No payment found", ""));
          }
            List<PaymentResponse> paymentResponses = paymentRepository.findAll().stream()
                    .map(payment -> {
                        PaymentResponse paymentResponse = new PaymentResponse();
                        paymentResponse.setPaymentId(payment.getPaymentId());
                        paymentResponse.setUserId(payment.getUserId());
                        paymentResponse.setOrderId(payment.getOrder().getOrderId());
                        paymentResponse.setPaymentMethod(payment.getPaymentMethod().getName());
                        paymentResponse.setTotal(payment.getAmountPaid());
                        paymentResponse.setStatus(payment.getStatus());
                        paymentResponse.setCreatedAt(payment.getCreatedAt());
                        paymentResponse.setPaymentDate(payment.getPaymentDate());
                        paymentResponse.setOrderCode(payment.getOrderCode());
                        paymentResponse.setOrderInfo(payment.getOrderInfo());
                        return paymentResponse;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new APIRespone(true, "Success", paymentResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getPaymentById(Long paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Payment not found", ""));
        }
        List<PaymentResponse> paymentResponses = paymentRepository.findById(paymentId).stream()
                .map(payment -> {
                    PaymentResponse paymentResponse = new PaymentResponse();
                    paymentResponse.setPaymentId(payment.getPaymentId());
                    paymentResponse.setUserId(payment.getUserId());
                    paymentResponse.setOrderId(payment.getOrder().getOrderId());
                    paymentResponse.setPaymentMethod(payment.getPaymentMethod().getName());
                    paymentResponse.setTotal(payment.getAmountPaid());
                    paymentResponse.setStatus(payment.getStatus());
                    paymentResponse.setCreatedAt(payment.getCreatedAt());
                    paymentResponse.setPaymentDate(payment.getPaymentDate());
                    paymentResponse.setOrderCode(payment.getOrderCode());
                    paymentResponse.setOrderInfo(payment.getOrderInfo());
                    return paymentResponse;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", paymentResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getPaymentDetailByPaymentId(Long paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Payment not found", ""));
        }
        List<PaymentDetailResponse> paymentDetailResponses = paymentDetailRepository.findByPaymentId(paymentId).stream()
                .map(paymentDetail -> {
                    PaymentDetailResponse paymentDetailResponse = new PaymentDetailResponse();
                    paymentDetailResponse.setPaymentDetailId(paymentDetail.getId());
                    paymentDetailResponse.setPaymentId(paymentDetail.getPayment().getPaymentId());
                    paymentDetailResponse.setTotal(paymentDetail.getTotalAmount());
                    paymentDetailResponse.setStatus(paymentDetail.getPaymentStatus());
                    paymentDetailResponse.setOrderId(paymentDetail.getOrder().getOrderId());
                    paymentDetailResponse.setStoreId(paymentDetail.getStore().getStoreId());
                    return paymentDetailResponse;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", paymentDetailResponses));
    }


}


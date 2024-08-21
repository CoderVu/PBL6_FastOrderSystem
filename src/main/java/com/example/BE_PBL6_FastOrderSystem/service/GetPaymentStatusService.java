package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.OrderRequestDTO;
import com.example.BE_PBL6_FastOrderSystem.utils.Helper.MoMoHelper;
import com.example.BE_PBL6_FastOrderSystem.utils.constants.MoMoConstant;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class GetPaymentStatusService {

    public Map<String, Object> getStatus(OrderRequestDTO requestDTO) throws IOException {

        JSONObject json = new JSONObject();
        json.put("partnerCode", MoMoConstant.PARTNER_CODE);
        json.put("accessKey", MoMoConstant.ACCESS_KEY);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("orderId", requestDTO.getOrderId());
        json.put("cardId", requestDTO.getCartIds());
        json.put("requestType", MoMoConstant.CHECK_STATUS_TYPE);

        String data = "partnerCode=" + MoMoConstant.PARTNER_CODE
                + "&accessKey=" + json.get("accessKey")
                + "&requestId=" + json.get("requestId")
                + "&orderId=" + json.get("orderId")
                + "&cardId=" + json.get("cardId")
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

}

package com.audistore.audi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZaloPayService {
    @Value("${zalopay.appId}") private int appId;
    @Value("${zalopay.key1}") private String key1;
    @Value("${zalopay.key2}") private String key2;
    @Value("${zalopay.createUrl}") private String createUrl;
    @Value("${zalopay.gatewayUrl}") private String gatewayUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    public String createOrder(Long orderId, long amountVnd, String returnUrl, String cancelUrl, String callbackUrl) {
        try {
            String appUser = "audi-store";
            long appTime = System.currentTimeMillis();
            String appTransId = genAppTransId(orderId);

            Map<String, Object> embed = new HashMap<>();
            embed.put("redirecturl", returnUrl);
            embed.put("cancelurl", cancelUrl);
            embed.put("orderId", orderId);

            String embedData = mapper.writeValueAsString(embed);
            String items = "[]";

            Map<String, Object> order = new LinkedHashMap<>();
            order.put("app_id", appId);
            order.put("app_trans_id", appTransId);
            order.put("app_time", appTime);
            order.put("app_user", appUser);
            order.put("amount", amountVnd);
            order.put("description", "Thanh toan don hang " + orderId);
            order.put("bank_code", "");
            order.put("item", items);
            order.put("embed_data", embedData);
            order.put("callback_url", callbackUrl);

            String data = appId + "|" + appTransId + "|" + appUser + "|" + amountVnd + "|" + appTime + "|" + embedData + "|" + items;
            order.put("mac", hmacSHA256(key1, data));

            // JSON body for API v2
            String jsonBody = mapper.writeValueAsString(order);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(createUrl))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) throw new RuntimeException("ZaloPay create order failed: " + resp.body());

            Map<?, ?> json = mapper.readValue(resp.body(), Map.class);
            Object url = json.get("order_url");
            if (url == null) url = json.get("zp_trans_token_url");
            if (url == null) throw new RuntimeException("Missing order_url");
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("Loi tao don ZaloPay", e);
        }
    }

    public String createGatewayUrl(Long orderId, long amountVnd, String returnUrl, String cancelUrl, String callbackUrl) {
        try {
            String appUser = "audi-store";
            long appTime = System.currentTimeMillis();
            String appTransId = genAppTransId(orderId);

            Map<String, Object> embed = new HashMap<>();
            embed.put("redirecturl", returnUrl);
            embed.put("cancelurl", cancelUrl);
            embed.put("orderId", orderId);

            String embedData = mapper.writeValueAsString(embed);
            String items = "[]";

            Map<String, Object> order = new LinkedHashMap<>();
            order.put("app_id", appId);
            order.put("app_trans_id", appTransId);
            order.put("app_time", appTime);
            order.put("app_user", appUser);
            order.put("amount", amountVnd);
            order.put("description", "Thanh toan don hang " + orderId);
            order.put("bank_code", ""); // Để hiển thị form thanh toán
            order.put("item", items);
            order.put("embed_data", embedData);
            order.put("callback_url", callbackUrl);

            String data = appId + "|" + appTransId + "|" + appUser + "|" + amountVnd + "|" + appTime + "|" + embedData + "|" + items;
            order.put("mac", hmacSHA256(key1, data));

            // Sử dụng createUrl (API v1) để tạo order và lấy order_url
            String jsonBody = mapper.writeValueAsString(order);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(createUrl))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) throw new RuntimeException("ZaloPay create order failed: " + resp.body());

            Map<?, ?> json = mapper.readValue(resp.body(), Map.class);
            Object url = json.get("order_url");
            if (url == null) url = json.get("zp_trans_token_url");
            if (url == null) throw new RuntimeException("Missing order_url");
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("Loi tao URL Gateway ZaloPay", e);
        }
    }

    // New method for creating URL with order configuration data (like VNPay)
    public String createGatewayUrlWithConfig(Long idCauHinh, Long idDaiLy, java.math.BigDecimal tienDatCoc, 
                                           String ghiChu, Long idKhuyenMai, String returnUrl, String cancelUrl, String callbackUrl) {
        try {
            String appUser = "audi-store";
            long appTime = System.currentTimeMillis();
            String appTransId = genAppTransId(null); // No orderId yet

            Map<String, Object> embed = new HashMap<>();
            embed.put("redirecturl", returnUrl);
            embed.put("cancelurl", cancelUrl);
            embed.put("idCauHinh", idCauHinh);
            embed.put("idDaiLy", idDaiLy);
            embed.put("tienDatCoc", tienDatCoc);
            embed.put("ghiChu", ghiChu);
            embed.put("idKhuyenMai", idKhuyenMai);

            String embedData = mapper.writeValueAsString(embed);
            String items = "[]";

            Map<String, Object> order = new LinkedHashMap<>();
            order.put("app_id", appId);
            order.put("app_trans_id", appTransId);
            order.put("app_time", appTime);
            order.put("app_user", appUser);
            order.put("amount", tienDatCoc.longValue());
            order.put("description", "Thanh toan dat coc xe");
            order.put("bank_code", ""); // Để hiển thị form thanh toán
            order.put("item", items);
            order.put("embed_data", embedData);
            order.put("callback_url", callbackUrl);

            String data = appId + "|" + appTransId + "|" + appUser + "|" + tienDatCoc.longValue() + "|" + appTime + "|" + embedData + "|" + items;
            order.put("mac", hmacSHA256(key1, data));

            // Sử dụng createUrl (API v1) để tạo order và lấy order_url
            String jsonBody = mapper.writeValueAsString(order);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(createUrl))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) throw new RuntimeException("ZaloPay create order failed: " + resp.body());

            Map<?, ?> json = mapper.readValue(resp.body(), Map.class);
            Object url = json.get("order_url");
            if (url == null) url = json.get("zp_trans_token_url");
            if (url == null) throw new RuntimeException("Missing order_url");
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("Loi tao URL Gateway ZaloPay", e);
        }
    }

    public boolean verifyCallback(String data, String mac) {
        return hmacSHA256(key2, data).equals(mac);
    }

    public Map<String, Object> parseCallbackData(String data) {
        try {
            // ZaloPay callback data is JSON string, not URL-encoded
            Map<String, Object> result = mapper.readValue(data, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            
            // Extract embed_data if present
            String embedDataStr = (String) result.get("embed_data");
            if (embedDataStr != null && !embedDataStr.isEmpty()) {
                try {
                    Map<String, Object> embedData = mapper.readValue(embedDataStr, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    result.put("embed_data", embedData);
                } catch (Exception e) {
                    // If embed_data is not JSON, keep as string
                    System.err.println("embed_data is not valid JSON: " + embedDataStr);
                }
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing callback data", e);
        }
    }

    private String genAppTransId(Long orderId) {
        String yymmdd = new SimpleDateFormat("yyMMdd").format(new Date());
        int rand = new Random().nextInt(900000) + 100000;
        String orderIdStr = orderId != null ? orderId.toString() : "config";
        return yymmdd + "_" + orderIdStr + "_" + rand;
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) { String h = Integer.toHexString(0xff & b); if (h.length()==1) sb.append('0'); sb.append(h); }
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
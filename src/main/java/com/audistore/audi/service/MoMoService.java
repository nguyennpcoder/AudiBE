package com.audistore.audi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MoMoService {

    @Value("${momo.partnerCode}") private String partnerCode;
    @Value("${momo.accessKey}") private String accessKey;
    @Value("${momo.secretKey}") private String secretKey;
    @Value("${momo.createUrl}") private String createUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    public String createOrder(Long orderId, long amountVnd, String returnUrl, String ipnUrl) {
        try {
            String requestId = String.valueOf(System.currentTimeMillis());
            String orderInfo = "Thanh toan don hang " + orderId;
            String requestType = "captureWallet"; // MoMo test wallet
            String orderIdStr = partnerCode + "_" + orderId + "_" + requestId;

            // raw signature string per MoMo docs
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amountVnd +
                    "&extraData=" + "" +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderIdStr +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + returnUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(secretKey, rawSignature);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("partnerCode", partnerCode);
            payload.put("accessKey", accessKey);
            payload.put("requestId", requestId);
            payload.put("amount", String.valueOf(amountVnd));
            payload.put("orderId", orderIdStr);
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", returnUrl);
            payload.put("ipnUrl", ipnUrl);
            payload.put("extraData", "");
            payload.put("requestType", requestType);
            payload.put("lang", "vi");
            payload.put("signature", signature);

            System.out.println("=== MoMo Create Order Request ===");
            System.out.println("Request URL: " + createUrl);
            System.out.println("Payload: " + payload);
            System.out.println("===============================");

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(createUrl))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();
            java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) throw new RuntimeException("MoMo create order failed: " + resp.body());

            System.out.println("=== MoMo Create Order Response ===");
            System.out.println("Status code: " + resp.statusCode());
            System.out.println("Response body: " + resp.body());
            System.out.println("================================");

            Map<String, Object> json = mapper.readValue(resp.body(), new TypeReference<Map<String, Object>>() {});
            Object url = json.get("payUrl");
            if (url == null) url = json.get("deeplink");
            if (url == null) throw new RuntimeException("Missing payUrl");
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("Loi tao don MoMo", e);
        }
    }

    // New method for creating order with configuration data (like VNPay)
    public String createOrderWithConfig(Long idCauHinh, Long idDaiLy, java.math.BigDecimal tienDatCoc, 
                                      String ghiChu, Long idKhuyenMai, String returnUrl, String ipnUrl) {
        try {
            String requestId = String.valueOf(System.currentTimeMillis());
            String orderInfo = "Thanh toan dat coc xe";
            String requestType = "captureWallet"; // MoMo test wallet
            String orderIdStr = partnerCode + "_config_" + requestId;

            // Encode order configuration in extraData
            String extraData = String.format("idCauHinh=%d&idDaiLy=%d&tienDatCoc=%s&ghiChu=%s&idKhuyenMai=%s",
                    idCauHinh, idDaiLy, tienDatCoc, ghiChu != null ? ghiChu : "", idKhuyenMai != null ? idKhuyenMai : "");

            // raw signature string per MoMo docs
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + tienDatCoc.longValue() +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderIdStr +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + returnUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(secretKey, rawSignature);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("partnerCode", partnerCode);
            payload.put("accessKey", accessKey);
            payload.put("requestId", requestId);
            payload.put("amount", String.valueOf(tienDatCoc.longValue()));
            payload.put("orderId", orderIdStr);
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", returnUrl);
            payload.put("ipnUrl", ipnUrl);
            payload.put("extraData", extraData);
            payload.put("requestType", requestType);
            payload.put("lang", "vi");
            payload.put("signature", signature);

            System.out.println("=== MoMo Create Config Order Request ===");
            System.out.println("Request URL: " + createUrl);
            System.out.println("Payload: " + payload);
            System.out.println("ExtraData: " + extraData);
            System.out.println("=====================================");

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(createUrl))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();
            java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) throw new RuntimeException("MoMo create order failed: " + resp.body());

            System.out.println("=== MoMo Create Config Order Response ===");
            System.out.println("Status code: " + resp.statusCode());
            System.out.println("Response body: " + resp.body());
            System.out.println("======================================");

            Map<String, Object> json = mapper.readValue(resp.body(), new TypeReference<Map<String, Object>>() {});
            Object url = json.get("payUrl");
            if (url == null) url = json.get("deeplink");
            if (url == null) throw new RuntimeException("Missing payUrl");
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("Loi tao don MoMo", e);
        }
    }

    public boolean verifyIpn(Map<String, String> params) {
        // MoMo IPN verification based on provided signature field
        try {
            String signature = params.get("signature");
            if (signature == null) {
                System.out.println("MoMo signature verification failed: no signature provided");
                return false;
            }

            // Construct raw hash string in the exact documented order
            String raw = "accessKey=" + accessKey +
                    "&amount=" + params.getOrDefault("amount", "") +
                    "&extraData=" + params.getOrDefault("extraData", "") +
                    "&message=" + params.getOrDefault("message", "") +
                    "&orderId=" + params.getOrDefault("orderId", "") +
                    "&orderInfo=" + params.getOrDefault("orderInfo", "") +
                    "&orderType=" + params.getOrDefault("orderType", "") +
                    "&partnerCode=" + params.getOrDefault("partnerCode", "") +
                    "&payType=" + params.getOrDefault("payType", "") +
                    "&requestId=" + params.getOrDefault("requestId", "") +
                    "&responseTime=" + params.getOrDefault("responseTime", "") +
                    "&resultCode=" + params.getOrDefault("resultCode", "") +
                    "&transId=" + params.getOrDefault("transId", "");

            String mySig = hmacSHA256(secretKey, raw);
            
            System.out.println("=== MoMo Signature Verification ===");
            System.out.println("Raw string: " + raw);
            System.out.println("Expected signature: " + signature);
            System.out.println("Calculated signature: " + mySig);
            System.out.println("Signatures match: " + mySig.equals(signature));
            System.out.println("==================================");
            
            return mySig.equals(signature);
        } catch (Exception e) {
            System.err.println("Error in MoMo signature verification: " + e.getMessage());
            return false;
        }
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
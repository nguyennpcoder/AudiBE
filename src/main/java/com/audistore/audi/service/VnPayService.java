package com.audistore.audi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String secretKey;

    @Value("${vnpay.payUrl}")
    private String vnpPayUrl;

    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_CURR_CODE = "VND";

    private String encodeRFC3986(String s) {
        String e = URLEncoder.encode(s, StandardCharsets.UTF_8);
        // RFC 3986 normalize (URLEncoder uses + for space, VNPay expects %20)
        return e.replace("+", "%20")
                .replace("%7E", "~");
    }

    public String createPaymentUrl(Long orderId, long amountVnd, String returnUrl, String clientIp) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", VNP_VERSION);
            vnpParams.put("vnp_Command", VNP_COMMAND);
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_CurrCode", VNP_CURR_CODE);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_Amount", String.valueOf(amountVnd * 100)); // VNPAY expects amount * 100
            vnpParams.put("vnp_IpAddr", clientIp);
            vnpParams.put("vnp_TxnRef", genTxnRef(orderId));
            vnpParams.put("vnp_OrderInfo", "orderId=" + orderId);
            vnpParams.put("vnp_OrderType", "other");

            String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 15);
            String expireDate = new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime());

            vnpParams.put("vnp_CreateDate", createDate);
            vnpParams.put("vnp_ExpireDate", expireDate);
            vnpParams.put("vnp_ReturnUrl", returnUrl);

            // Sort and build querystring
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            StringBuilder query = new StringBuilder();
            StringBuilder hashData = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    String ek = encodeRFC3986(fieldName);
                    String ev = encodeRFC3986(fieldValue);
                    query.append(ek).append("=").append(ev);
                    hashData.append(ek).append("=").append(ev);
                    if (i < fieldNames.size() - 1) {
                        query.append("&");
                        hashData.append("&");
                    }
                }
            }

            String secureHash = hmacSHA512(secretKey, hashData.toString());
            String paymentUrl = vnpPayUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
            return paymentUrl;
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi tạo URL VNPay", ex);
        }
    }

    public boolean validateSignature(Map<String, String> allParams) {
        String vnpSecureHash = allParams.get("vnp_SecureHash");
        // Only include VNPay params (vnp_*) for signature verification
        Map<String, String> vnpParams = new HashMap<>();
        for (Map.Entry<String, String> e : allParams.entrySet()) {
            String key = e.getKey();
            if (key != null && key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                vnpParams.put(key, e.getValue());
            }
        }

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append("=").append(fieldValue);
                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                }
            }
        }
        String myHash = hmacSHA512(secretKey, hashData.toString());
        return myHash.equalsIgnoreCase(vnpSecureHash);
    }

    private String genTxnRef(Long orderId) {
        return orderId + "-" + System.currentTimeMillis();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hash.append('0');
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC SHA512 error", e);
        }
    }
}

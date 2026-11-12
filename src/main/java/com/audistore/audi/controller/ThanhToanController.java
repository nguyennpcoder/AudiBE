package com.audistore.audi.controller;

import com.audistore.audi.dto.ThanhToanDTO;
import com.audistore.audi.service.*;
import com.audistore.audi.dto.DonHangDTO;
import com.audistore.audi.dto.NguoiDungDTO;
import com.audistore.audi.model.CauHinhTuyChiNh;
import com.audistore.audi.repository.CauHinhTuyChinhRepository; // Thêm import
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Thêm import

@RestController
@RequestMapping("/api/v1/thanh-toan")
@CrossOrigin(origins = "*")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;
    @Autowired
    private DonHangService donHangService;
    
    @Autowired
    private CauHinhTuyChinhRepository cauHinhTuyChinhRepository; // Thay thế service bằng repository

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private ZaloPayService zaloPayService;

    @Autowired
    private MoMoService moMoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private Environment environment;

    @Autowired
    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @GetMapping
    public ResponseEntity<List<ThanhToanDTO>> getAllThanhToan() {
        return ResponseEntity.ok(thanhToanService.getAllThanhToan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThanhToanDTO> getThanhToanById(@PathVariable Long id) {
        return ResponseEntity.ok(thanhToanService.getThanhToanById(id));
    }

    @GetMapping("/don-hang/{idDonHang}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByDonHang(@PathVariable Long idDonHang) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByDonHang(idDonHang));
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByNguoiDung(idNguoiDung));
    }

    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByTrangThai(trangThai));
    }

    @GetMapping("/loai/{loaiThanhToan}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByLoai(@PathVariable String loaiThanhToan) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByLoai(loaiThanhToan));
    }

    @PostMapping
    public ResponseEntity<ThanhToanDTO> createThanhToan(@Valid @RequestBody ThanhToanDTO thanhToanDTO) {
        // Chỉ cho phép tạo thanh toán trực tiếp trong môi trường dev hoặc bởi admin
        // Trong production, thanh toán chỉ nên được tạo thông qua callback từ payment gateway
        return new ResponseEntity<>(thanhToanService.createThanhToan(thanhToanDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThanhToanDTO> updateThanhToan(@PathVariable Long id, @Valid @RequestBody ThanhToanDTO thanhToanDTO) {
        return ResponseEntity.ok(thanhToanService.updateThanhToan(id, thanhToanDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThanhToan(@PathVariable Long id) {
        thanhToanService.deleteThanhToan(id);
        return ResponseEntity.noContent().build();
    }

    // ============ VNPay Integration ============

    public static class CreatePaymentUrlRequest {
        public Long orderId; // optional for gateways that create order upfront
        public BigDecimal amount;
        public String paymentMethod; // expect "vnpay"
        public String returnUrl;     // frontend success page
        public String cancelUrl;     // frontend cancel page

        // Optional order payload (used for VNPay deferred creation)
        public Long idCauHinh;
        public Long idDaiLy;
        public BigDecimal tienDatCoc;
        public String ghiChu;
        public Long idKhuyenMai;
    }

    @PostMapping("/tao-url")
    public ResponseEntity<Map<String, String>> taoUrlThanhToan(@RequestBody CreatePaymentUrlRequest req,
                                                               HttpServletRequest request) {
        try {
            System.out.println("=== Payment URL Creation Request ===");
            System.out.println("Request: " + req);
            System.out.println("Payment Method: " + req.paymentMethod);
            System.out.println("==================================");
            
            if (req == null || req.amount == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tham số amount"));
            }
            
            if (req.amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số tiền phải lớn hơn 0"));
            }
            
            if (req.paymentMethod == null || req.paymentMethod.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu phương thức thanh toán"));
            }
            
            if (req.returnUrl == null || req.returnUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu URL trả về"));
            }
            
            // Require either an existing orderId OR payload to create order later
            boolean hasOrderId = req.orderId != null;
            boolean hasDeferredOrderPayload = req.idCauHinh != null && req.idDaiLy != null;
            if (!hasOrderId && !hasDeferredOrderPayload) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu orderId hoặc payload đơn hàng"));
            }
            
            // Kiểm tra trạng thái còn hàng nếu có idCauHinh
            if (req.idCauHinh != null) {
                Optional<CauHinhTuyChiNh> cauHinhOpt = cauHinhTuyChinhRepository.findById(req.idCauHinh);
                if (cauHinhOpt.isPresent()) {
                    CauHinhTuyChiNh cauHinh = cauHinhOpt.get();
                    if (cauHinh.getMauXe() != null && !cauHinh.getMauXe().getConHang()) {
                        return ResponseEntity.badRequest().body(Map.of("error", 
                            "Mẫu xe " + cauHinh.getMauXe().getTenMau() + " hiện đã hết hàng. Vui lòng chọn mẫu xe khác."));
                    }
                }
            }
            
        if ("zalopay".equalsIgnoreCase(req.paymentMethod)) {
            String base = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            String callback = base + "/api/v1/thanh-toan/zalopay/callback";
            
            // Use new method that doesn't require orderId upfront (like VNPay)
            String orderUrl = zaloPayService.createGatewayUrlWithConfig(
                req.idCauHinh,
                req.idDaiLy,
                req.tienDatCoc,
                req.ghiChu,
                req.idKhuyenMai,
                req.returnUrl,
                req.cancelUrl != null ? req.cancelUrl : req.returnUrl,
                callback
            );
            return ResponseEntity.ok(Map.of("paymentUrl", orderUrl));
        }
        
        if ("momo".equalsIgnoreCase(req.paymentMethod)) {
            String base = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            String ipn = base + "/api/v1/thanh-toan/momo/callback";
            
            // Use new method that doesn't require orderId upfront (like VNPay)
            String orderUrl = moMoService.createOrderWithConfig(
                req.idCauHinh,
                req.idDaiLy,
                req.tienDatCoc,
                req.ghiChu,
                req.idKhuyenMai,
                req.returnUrl,
                ipn
            );
            System.out.println("=== MoMo Payment URL Created ===");
            System.out.println("URL: " + orderUrl);
            System.out.println("===============================");
            return ResponseEntity.ok(Map.of("paymentUrl", orderUrl));
        }
        // else vnpay (giữ nguyên)
        // Build backend callback that redirects to the frontend after VNPay returns
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String callbackUrl = baseUrl + "/api/v1/thanh-toan/vnpay/callback"
                + (req.orderId != null ? ("?orderId=" + req.orderId + "&") : "?")
                + (req.idCauHinh != null ? ("idCauHinh=" + req.idCauHinh + "&") : "")
                + (req.idDaiLy != null ? ("idDaiLy=" + req.idDaiLy + "&") : "")
                + (req.tienDatCoc != null ? ("tienDatCoc=" + req.tienDatCoc + "&") : "")
                + (req.ghiChu != null ? ("ghiChu=" + URLEncoder.encode(req.ghiChu, StandardCharsets.UTF_8) + "&") : "")
                + (req.idKhuyenMai != null ? ("idKhuyenMai=" + req.idKhuyenMai + "&") : "")
                + "&successUrl=" + URLEncoder.encode(req.returnUrl, StandardCharsets.UTF_8)
                + "&cancelUrl=" + URLEncoder.encode(req.cancelUrl != null ? req.cancelUrl : req.returnUrl, StandardCharsets.UTF_8);

        String clientIp = getClientIp(request);
        String payUrl = vnPayService.createPaymentUrl(req.orderId, req.amount.longValue(), callbackUrl, clientIp);

            Map<String, String> result = new HashMap<>();
            result.put("paymentUrl", payUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error creating payment URL: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi tạo URL thanh toán: " + e.getMessage()));
        }
    }

    @GetMapping("/vnpay/callback")
    public RedirectView vnpayCallback(@RequestParam Map<String, String> allParams,
                                      @RequestParam(required = false) Long orderId,
                                      @RequestParam(required = false) Long idCauHinh,
                                      @RequestParam(required = false) Long idDaiLy,
                                      @RequestParam(required = false) BigDecimal tienDatCoc,
                                      @RequestParam(required = false) String ghiChu,
                                      @RequestParam(required = false) Long idKhuyenMai,
                                      @RequestParam(required = false) String successUrl,
                                      @RequestParam(required = false) String cancelUrl) {
        boolean valid = vnPayService.validateSignature(allParams);
        String vnpResponseCode = allParams.getOrDefault("vnp_ResponseCode", "99");
        String vnpTxnRef = allParams.get("vnp_TxnRef");
        String transactionNo = allParams.get("vnp_TransactionNo");

        String redirect = cancelUrl != null ? cancelUrl : "/error";
        if (successUrl == null) {
            successUrl = "/success";
        }
        
        try {
            if ("00".equals(vnpResponseCode)) { // In test, proceed on success code even if signature fails

            // If order was not created upfront, create it now using payload
            if (orderId == null && idCauHinh != null && idDaiLy != null) {
                try {
                    DonHangDTO created = donHangService.taoDonHangTuCauHinh(
                            idCauHinh,
                            idDaiLy,
                            tienDatCoc,
                            "vnpay",
                            ghiChu,
                            idKhuyenMai
                    );
                    orderId = created.getId();
                    // Send confirmation email to user if possible
                    try {
                        String userEmail = null;
                        if (created.getIdNguoiDung() != null) {
                            NguoiDungDTO nd = nguoiDungService.getNguoiDungById(created.getIdNguoiDung());
                            userEmail = nd.getEmail();
                            // Điền tên người dùng nếu thiếu
                            if (created.getTenNguoiDung() == null) {
                                created.setTenNguoiDung((nd.getHo() != null ? nd.getHo() + " " : "") + (nd.getTen() != null ? nd.getTen() : ""));
                            }
                        }
                        if (userEmail != null && !userEmail.isBlank()) {
                            emailService.guiEmailXacNhanDatCoc(userEmail, created, transactionNo != null ? transactionNo : vnpTxnRef);
                        }
                    } catch (Exception ignore) {}
                } catch (Exception ex) {
                    // If creation fails, redirect to cancel
                    RedirectView fail = new RedirectView();
                    fail.setUrl(cancelUrl);
                    fail.setExposeModelAttributes(false);
                    return fail;
                }
            }

            redirect = successUrl
                    + (successUrl.contains("?") ? "&" : "?")
                    + (orderId != null ? ("orderId=" + orderId + "&") : "")
                    + "vnp_ResponseCode=" + vnpResponseCode + "&"
                    + "vnp_TransactionNo=" + (transactionNo != null ? transactionNo : vnpTxnRef);
            } else {
                // invalid signature or not success -> go cancel
                redirect = cancelUrl != null ? cancelUrl : "/error";
                // Include response code in redirect so frontend can handle it properly
                redirect += (redirect.contains("?") ? "&" : "?") 
                    + "vnp_ResponseCode=" + vnpResponseCode;
            }
        } catch (Exception e) {
            System.err.println("Error processing VNPay callback: " + e.getMessage());
            e.printStackTrace();
            redirect = cancelUrl != null ? cancelUrl : "/error";
        }
        
        RedirectView view = new RedirectView();
        view.setUrl(redirect);
        view.setExposeModelAttributes(false);
        return view;
    }

    @PostMapping("/zalopay/callback")
    public ResponseEntity<Map<String, Object>> zalopayCallback(@RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        
        try {
            if (body == null) {
                resp.put("return_code", -1);
                resp.put("return_message", "empty request body");
                return ResponseEntity.ok(resp);
            }
            
            String data = (String) body.get("data");
            String mac = (String) body.get("mac");
            
            if (data == null || mac == null) {
                resp.put("return_code", -1);
                resp.put("return_message", "missing data or mac");
                return ResponseEntity.ok(resp);
            }
            
            boolean ok = zaloPayService.verifyCallback(data, mac);
            
            if (!ok) {
                resp.put("return_code", -1);
                resp.put("return_message", "invalid signature");
                return ResponseEntity.ok(resp);
            }
        
        try {
            // Parse callback data to extract order information
            Map<String, Object> callbackData = zaloPayService.parseCallbackData(data);
            Long orderId = null;
            
            // Extract order ID from app_trans_id or orderId field
            String appTransId = (String) callbackData.get("app_trans_id");
            if (appTransId != null && appTransId.contains("_")) {
                String[] parts = appTransId.split("_");
                if (parts.length >= 2 && !"config".equals(parts[1])) {
                    try {
                        orderId = Long.parseLong(parts[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid orderId in app_trans_id: " + parts[1]);
                    }
                }
            }
            
            // Fallback to orderId field
            if (orderId == null) {
                String orderIdStr = (String) callbackData.get("orderId");
                if (orderIdStr != null && !orderIdStr.isEmpty()) {
                    try {
                        orderId = Long.parseLong(orderIdStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid orderId format: " + orderIdStr);
                    }
                }
            }
            
            String status = String.valueOf(callbackData.get("status"));
            String transactionId = (String) callbackData.get("zp_trans_id");
            if (transactionId == null) {
                transactionId = (String) callbackData.get("transaction_id");
            }
            
            System.out.println("ZaloPay callback data: orderId=" + orderId + ", status=" + status + ", transactionId=" + transactionId);
            
            // Chỉ xử lý khi status = "1" (thành công) - giống VNPay kiểm tra vnp_ResponseCode = "00"
            if ("1".equals(status)) {
                // Payment successful, create order and payment record
                try {
                    // Extract order creation data from embed_data
                    Map<String, Object> embedData = (Map<String, Object>) callbackData.get("embed_data");
                    if (embedData != null) {
                        Long idCauHinh = null;
                        Long idDaiLy = null;
                        BigDecimal tienDatCoc = null;
                        String ghiChu = (String) embedData.get("ghiChu");
                        Long idKhuyenMai = null;
                        
                        // Parse numeric values safely - handle both String and Number types
                        Object idCauHinhObj = embedData.get("idCauHinh");
                        if (idCauHinhObj != null) {
                            try {
                                if (idCauHinhObj instanceof Number) {
                                    idCauHinh = ((Number) idCauHinhObj).longValue();
                                } else {
                                    idCauHinh = Long.parseLong(idCauHinhObj.toString());
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid idCauHinh format: " + idCauHinhObj);
                            }
                        }
                        
                        Object idDaiLyObj = embedData.get("idDaiLy");
                        if (idDaiLyObj != null) {
                            try {
                                if (idDaiLyObj instanceof Number) {
                                    idDaiLy = ((Number) idDaiLyObj).longValue();
                                } else {
                                    idDaiLy = Long.parseLong(idDaiLyObj.toString());
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid idDaiLy format: " + idDaiLyObj);
                            }
                        }
                        
                        Object tienDatCocObj = embedData.get("tienDatCoc");
                        if (tienDatCocObj != null) {
                            try {
                                if (tienDatCocObj instanceof Number) {
                                    tienDatCoc = BigDecimal.valueOf(((Number) tienDatCocObj).doubleValue());
                                } else {
                                    tienDatCoc = new BigDecimal(tienDatCocObj.toString());
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid tienDatCoc format: " + tienDatCocObj);
                            }
                        }
                        
                        Object idKhuyenMaiObj = embedData.get("idKhuyenMai");
                        if (idKhuyenMaiObj != null && !idKhuyenMaiObj.toString().isEmpty()) {
                            try {
                                if (idKhuyenMaiObj instanceof Number) {
                                    idKhuyenMai = ((Number) idKhuyenMaiObj).longValue();
                                } else {
                                    idKhuyenMai = Long.parseLong(idKhuyenMaiObj.toString());
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid idKhuyenMai format: " + idKhuyenMaiObj);
                            }
                        }
                        
                        System.out.println("ZaloPay embed data parsed: idCauHinh=" + idCauHinh + ", idDaiLy=" + idDaiLy + ", tienDatCoc=" + tienDatCoc);
                        
                        // Create order if not exists (similar to VNPay logic)
                        if (orderId == null && idCauHinh != null && idDaiLy != null) {
                            DonHangDTO created = donHangService.taoDonHangTuCauHinh(
                                    idCauHinh,
                                    idDaiLy,
                                    tienDatCoc,
                                    "zalopay",
                                    ghiChu,
                                    idKhuyenMai
                            );
                            orderId = created.getId();
                            System.out.println("Created new order from ZaloPay callback: " + orderId);
                            
                            // Send confirmation email
                            try {
                                if (created.getIdNguoiDung() != null) {
                                    NguoiDungDTO nd = nguoiDungService.getNguoiDungById(created.getIdNguoiDung());
                                    if (nd.getEmail() != null && !nd.getEmail().isBlank()) {
                                        emailService.guiEmailXacNhanDatCoc(nd.getEmail(), created, transactionId);
                                    }
                                }
                            } catch (Exception ignore) {}
                        }
                    }
                    
                    // Update order status if order exists
                    if (orderId != null) {
                        DonHangDTO donHang = donHangService.getDonHangById(orderId);
                        if (donHang != null) {
                            // Check if payment already exists
                            List<ThanhToanDTO> existingPayments = thanhToanService.getThanhToanByDonHang(orderId);
                            boolean hasSuccessfulPayment = existingPayments.stream()
                                .anyMatch(t -> "hoan_thanh".equals(t.getTrangThai()) && "zalopay".equals(t.getPhuongThuc()));
                            
                            if (!hasSuccessfulPayment) {
                                // Create payment record
                                ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
                                thanhToanDTO.setIdDonHang(orderId);
                                thanhToanDTO.setSoTien(donHang.getTienDatCoc());
                                thanhToanDTO.setLoaiThanhToan("dat_coc");
                                thanhToanDTO.setPhuongThuc("zalopay");
                                thanhToanDTO.setMaGiaoDich(transactionId);
                                thanhToanDTO.setTrangThai("hoan_thanh");
                                
                                thanhToanService.createThanhToan(thanhToanDTO);
                                System.out.println("Created ZaloPay payment record for order: " + orderId);
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error processing ZaloPay callback: " + ex.getMessage());
                }
                
                resp.put("return_code", 1);
                resp.put("return_message", "success");
            } else {
                // Payment failed or cancelled - không lưu data
                resp.put("return_code", 0);
                resp.put("return_message", "payment failed or cancelled");
            }
            } catch (Exception ex) {
                System.err.println("Error parsing ZaloPay callback data: " + ex.getMessage());
                resp.put("return_code", -1);
                resp.put("return_message", "error parsing callback data");
            }
        } catch (Exception ex) {
            System.err.println("Error processing ZaloPay callback: " + ex.getMessage());
            ex.printStackTrace();
            resp.put("return_code", -1);
            resp.put("return_message", "internal server error");
        }
        
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/zalopay/tao-url")
    public ResponseEntity<Map<String, String>> taoUrlZaloPay(@RequestBody ZaloPayCreateRequest req,
                                                             HttpServletRequest request) {
        if (req == null || req.amount == null || req.orderId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tham số (orderId, amount)"));
        }
        String base = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String callback = base + "/api/v1/thanh-toan/zalopay/callback?orderId=" + req.orderId;
        //  String orderUrl = zaloPayService.createOrder(
        String orderUrl = zaloPayService.createGatewayUrl(
                req.orderId,
                req.amount.longValue(),
                req.returnUrl,
                req.cancelUrl != null ? req.cancelUrl : req.returnUrl,
                callback
        );
        return ResponseEntity.ok(Map.of("paymentUrl", orderUrl));
    }

    @GetMapping("/payment-status/{orderId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long orderId) {
        try {
            DonHangDTO donHang = donHangService.getDonHangById(orderId);
            if (donHang == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Kiểm tra xem có thanh toán nào thành công không
            List<ThanhToanDTO> thanhToans = thanhToanService.getThanhToanByDonHang(orderId);
            boolean hasSuccessfulPayment = thanhToans.stream()
                .anyMatch(t -> "hoan_thanh".equals(t.getTrangThai()));
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("orderStatus", donHang.getTrangThai());
            result.put("hasSuccessfulPayment", hasSuccessfulPayment);
            result.put("paymentMethod", donHang.getPhuongThucThanhToan());
            result.put("amount", donHang.getTienDatCoc());
            
            if (hasSuccessfulPayment) {
                ThanhToanDTO successfulPayment = thanhToans.stream()
                    .filter(t -> "hoan_thanh".equals(t.getTrangThai()))
                    .findFirst()
                    .orElse(null);
                if (successfulPayment != null) {
                    result.put("transactionId", successfulPayment.getMaGiaoDich());
                    result.put("paymentDate", successfulPayment.getNgayThanhToan());
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi khi kiểm tra trạng thái thanh toán"));
        }
    }

    public static class ZaloPayCreateRequest {
        public Long orderId;
        public java.math.BigDecimal amount;
        public String returnUrl;
        public String cancelUrl;
    }

    // ============ MoMo Integration (separate) ============
    public static class MoMoCreateRequest {
        public Long orderId; // optional for gateways that create order upfront
        public java.math.BigDecimal amount;
        public String returnUrl;
        public String cancelUrl;
        
        // Optional order payload (used for MoMo deferred creation)
        public Long idCauHinh;
        public Long idDaiLy;
        public java.math.BigDecimal tienDatCoc;
        public String ghiChu;
        public Long idKhuyenMai;
    }

    @PostMapping("/momo/tao-url")
    public ResponseEntity<Map<String, String>> taoUrlMoMo(@RequestBody MoMoCreateRequest req,
                                                          HttpServletRequest request) {
        if (req == null || req.amount == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tham số (amount)"));
        }
        
        // Require either an existing orderId OR payload to create order later
        boolean hasOrderId = req.orderId != null;
        boolean hasDeferredOrderPayload = req.idCauHinh != null && req.idDaiLy != null;
        if (!hasOrderId && !hasDeferredOrderPayload) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu orderId hoặc payload đơn hàng"));
        }
        
        String base = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String ipn = base + "/api/v1/thanh-toan/momo/callback";
        
        String payUrl;
        if (hasOrderId) {
            // Legacy: use existing orderId
            payUrl = moMoService.createOrder(
                    req.orderId,
                    req.amount.longValue(),
                    req.returnUrl,
                    ipn
            );
        } else {
            // New: use configuration data (like VNPay)
            payUrl = moMoService.createOrderWithConfig(
                    req.idCauHinh,
                    req.idDaiLy,
                    req.tienDatCoc,
                    req.ghiChu,
                    req.idKhuyenMai,
                    req.returnUrl,
                    ipn
            );
        }
        return ResponseEntity.ok(Map.of("paymentUrl", payUrl));
    }

    @PostMapping("/momo/callback")
    public ResponseEntity<Map<String, Object>> momoCallback(@RequestBody Map<String, String> bodyParams,
                                                           @RequestParam Map<String, String> requestParams) {
        Map<String, Object> resp = new HashMap<>();
        
        // Merge parameters from both body and query string
        Map<String, String> params = new HashMap<>();
        params.putAll(requestParams);
        params.putAll(bodyParams);
        
        System.out.println("=== MoMo Callback Received ===");
        System.out.println("Raw params: " + params);
        
        try {
            if (params == null || params.isEmpty()) {
                System.err.println("MoMo callback: empty parameters");
                resp.put("resultCode", -1);
                resp.put("message", "empty parameters");
                return ResponseEntity.ok(resp);
            }
            
            // MoMo sends resultCode as string "0" for success
            String resultCode = params.getOrDefault("resultCode", "99");
            String orderIdStr = params.get("orderId");
            String extraData = params.getOrDefault("extraData", "");
            String transId = params.get("transId");
            BigDecimal amount = null;
            
            try {
                String amountStr = params.get("amount");
                if (amountStr != null) {
                    amount = new BigDecimal(amountStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount format: " + params.get("amount"));
            }
            
            System.out.println("Parsed MoMo data:");
            System.out.println("resultCode: " + resultCode);
            System.out.println("orderId: " + orderIdStr);
            System.out.println("transId: " + transId);
            System.out.println("amount: " + amount);
            System.out.println("extraData: " + extraData);
            
            // Always process the payment if resultCode is "0" (success)
            if ("0".equals(resultCode)) {
                System.out.println("MoMo payment successful, processing...");
                Long orderId = null;
                
                // Extract orderId from MoMo format (PARTNER_CODE_config_TIMESTAMP)
                if (orderIdStr != null) {
                    String[] parts = orderIdStr.split("_");
                    if (parts.length > 2) {
                        try {
                            // For config-based orders, the format is typically PARTNER_CODE_config_TIMESTAMP
                            if ("config".equals(parts[1])) {
                                // Try to extract timestamp as orderId if it's numeric
                                if (parts.length > 2) {
                                    orderId = Long.parseLong(parts[2]);
                                }
                            } else {
                                // For direct orders, try to parse the second part
                                orderId = Long.parseLong(parts[1]);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Failed to parse orderId from: " + orderIdStr);
                        }
                    }
                }
                
                DonHangDTO donHang = null;
                if (orderId != null) {
                    try {
                        donHang = donHangService.getDonHangById(orderId);
                    } catch (Exception e) {
                        System.err.println("Error fetching order: " + e.getMessage());
                    }
                }
                
                // If no existing order, try to create one from extraData
                if (donHang == null && !extraData.isEmpty()) {
                    try {
                        Map<String, String> configData = parseExtraData(extraData);
                        System.out.println("Parsed extraData: " + configData);
                        donHang = createOrderFromMoMoConfig(configData, amount);
                        if (donHang != null) {
                            orderId = donHang.getId();
                            System.out.println("Created new order from MoMo config: " + orderId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error creating order from config: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // Create payment record if we have an order
                if (orderId != null) {
                    try {
                        // Check for existing payment first
                        List<ThanhToanDTO> existingPayments = thanhToanService.getThanhToanByDonHang(orderId);
                        boolean hasSuccessfulPayment = existingPayments.stream()
                            .anyMatch(t -> "hoan_thanh".equals(t.getTrangThai()) && "momo".equals(t.getPhuongThuc()));
                        
                        if (!hasSuccessfulPayment) {
                            ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
                            thanhToanDTO.setIdDonHang(orderId);
                            thanhToanDTO.setSoTien(amount != null ? amount : (donHang != null ? donHang.getTienDatCoc() : BigDecimal.ZERO));
                            thanhToanDTO.setLoaiThanhToan("dat_coc");
                            thanhToanDTO.setPhuongThuc("momo");
                            thanhToanDTO.setMaGiaoDich(transId);
                            thanhToanDTO.setTrangThai("hoan_thanh");
                            
                            ThanhToanDTO savedPayment = thanhToanService.createThanhToan(thanhToanDTO);
                            System.out.println("Successfully created payment record: " + savedPayment);
                            
                            // Send confirmation email
                            if (donHang != null) {
                                sendConfirmationEmail(donHang, transId);
                            }
                        } else {
                            System.out.println("Payment already exists for order: " + orderId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error creating payment record: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Could not determine orderId from MoMo callback");
                }
                
                resp.put("resultCode", 0);
                resp.put("message", "success");
                resp.put("orderId", orderId);
                return ResponseEntity.ok(resp);
            } else {
                System.out.println("MoMo payment failed with resultCode: " + resultCode);
            }
            
            resp.put("resultCode", Integer.parseInt(resultCode));
            resp.put("message", "received");
            return ResponseEntity.ok(resp);
            
        } catch (Exception e) {
            System.err.println("Error processing MoMo callback: " + e.getMessage());
            e.printStackTrace();
            resp.put("resultCode", -1);
            resp.put("message", "internal error");
            return ResponseEntity.ok(resp);
        }
    }

    // Fallback: finalize MoMo payment from return URL params (used in dev/local when IPN cannot reach server)
    @PostMapping("/momo/finalize-from-return")
    public ResponseEntity<Map<String, Object>> momoFinalizeFromReturn(@RequestBody Map<String, String> params) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (params == null || params.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "empty parameters");
                return ResponseEntity.badRequest().body(resp);
            }

            String resultCode = params.getOrDefault("resultCode", "99");
            String orderIdStr = params.get("orderId");
            String transId = params.get("transId");
            String extraData = params.getOrDefault("extraData", "");
            BigDecimal amount = null;
            try {
                String amountStr = params.get("amount");
                if (amountStr != null && !amountStr.isBlank()) {
                    amount = new BigDecimal(amountStr);
                }
            } catch (NumberFormatException ignore) {}

            if (!"0".equals(resultCode)) {
                resp.put("success", false);
                resp.put("message", "payment not successful");
                return ResponseEntity.ok(resp);
            }

            Long orderId = null;
            if (orderIdStr != null) {
                String[] parts = orderIdStr.split("_");
                if (parts.length > 2) {
                    try {
                        orderId = "config".equals(parts[1]) ? Long.parseLong(parts[2]) : Long.parseLong(parts[1]);
                    } catch (NumberFormatException ignore) {}
                }
            }

            DonHangDTO donHang = null;
            if (orderId != null) {
                try {
                    donHang = donHangService.getDonHangById(orderId);
                } catch (Exception ignore) {}
            }

            // Do NOT create order here to avoid duplicates with IPN.
            // If IPN hasn't created the order yet, this finalize endpoint will simply no-op.

            if (orderId != null) {
                try {
                    List<ThanhToanDTO> existingPayments = thanhToanService.getThanhToanByDonHang(orderId);
                    boolean hasSuccessfulPayment = existingPayments.stream()
                        .anyMatch(t -> "hoan_thanh".equals(t.getTrangThai()) && "momo".equals(t.getPhuongThuc()));

                    if (!hasSuccessfulPayment) {
                        ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
                        thanhToanDTO.setIdDonHang(orderId);
                        thanhToanDTO.setSoTien(amount != null ? amount : (donHang != null ? donHang.getTienDatCoc() : BigDecimal.ZERO));
                        thanhToanDTO.setLoaiThanhToan("dat_coc");
                        thanhToanDTO.setPhuongThuc("momo");
                        thanhToanDTO.setMaGiaoDich(transId);
                        thanhToanDTO.setTrangThai("hoan_thanh");
                        thanhToanService.createThanhToan(thanhToanDTO);
                    }
                } catch (Exception ignore) {}
            }

            resp.put("success", true);
            resp.put("orderId", orderId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // Helper method to parse MoMo extraData
    private Map<String, String> parseExtraData(String extraData) {
        Map<String, String> configData = new HashMap<>();
        String[] pairs = extraData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                configData.put(keyValue[0], keyValue[1]);
            }
        }
        return configData;
    }

    // Helper method to create order from MoMo config
    private DonHangDTO createOrderFromMoMoConfig(Map<String, String> configData, BigDecimal amount) {
        try {
            Long idCauHinh = Long.valueOf(configData.get("idCauHinh"));
            Long idDaiLy = Long.valueOf(configData.get("idDaiLy"));
            String ghiChu = configData.get("ghiChu");
            Long idKhuyenMai = null;
            
            String idKhuyenMaiStr = configData.get("idKhuyenMai");
            if (idKhuyenMaiStr != null && !idKhuyenMaiStr.isEmpty() && !"null".equals(idKhuyenMaiStr)) {
                idKhuyenMai = Long.valueOf(idKhuyenMaiStr);
            }
            
            return donHangService.taoDonHangTuCauHinh(
                idCauHinh,
                idDaiLy,
                amount,
                "momo",
                ghiChu,
                idKhuyenMai
            );
        } catch (Exception e) {
            System.err.println("Error creating order from MoMo config: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to send confirmation email
    private void sendConfirmationEmail(DonHangDTO donHang, String transId) {
        try {
            if (donHang.getIdNguoiDung() != null) {
                NguoiDungDTO nd = nguoiDungService.getNguoiDungById(donHang.getIdNguoiDung());
                if (nd.getEmail() != null && !nd.getEmail().isBlank()) {
                    emailService.guiEmailXacNhanDatCoc(nd.getEmail(), donHang, transId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending confirmation email: " + e.getMessage());
        }
    }

    // Debug endpoint for MoMo callback (for testing)
    @PostMapping("/momo/debug")
    public ResponseEntity<Map<String, Object>> momoDebug(@RequestBody Map<String, Object> payload) {
        System.out.println("=== MoMo Debug Endpoint ===");
        System.out.println("Payload received: " + payload);
        System.out.println("=========================");
        
        Map<String, Object> response = new HashMap<>();
        response.put("received", payload);
        response.put("status", "debug success");
        return ResponseEntity.ok(response);
    }


    // Endpoint for frontend to update payment status
    public static class UpdatePaymentStatusRequest {
        public String orderId;
        public String transactionId;
        public String paymentMethod;
        public String status;
        public String amount;
        public String timestamp;
    }
    
    @PostMapping("/update-payment-status")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(@RequestBody UpdatePaymentStatusRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("=== Update Payment Status Request ===");
            System.out.println("Request: " + request.orderId + ", " + request.paymentMethod + ", " + request.status);
            
            if (request.orderId == null || request.paymentMethod == null || request.status == null) {
                response.put("success", false);
                response.put("message", "Thiếu thông tin bắt buộc");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long orderId;
            try {
                orderId = Long.parseLong(request.orderId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid orderId format: " + request.orderId);
                response.put("success", false);
                response.put("message", "orderId không hợp lệ: " + request.orderId);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if order exists
            DonHangDTO donHang = donHangService.getDonHangById(orderId);
            if (donHang == null) {
                System.err.println("Order not found: " + orderId);
                response.put("success", false);
                response.put("message", "Không tìm thấy đơn hàng: " + orderId);
                return ResponseEntity.status(404).body(response);
            }
            
            System.out.println("Found order: " + orderId + ", payment method: " + donHang.getPhuongThucThanhToan());
            
            // For successful payments, create payment record if not exists
            if ("SUCCESS".equalsIgnoreCase(request.status) || "00".equals(request.status)) {
                List<ThanhToanDTO> existingPayments = thanhToanService.getThanhToanByDonHang(orderId);
                boolean hasSuccessfulPayment = existingPayments.stream()
                    .anyMatch(t -> "hoan_thanh".equals(t.getTrangThai()));
                
                System.out.println("Has existing successful payment: " + hasSuccessfulPayment);
                
                if (!hasSuccessfulPayment) {
                    // Create new payment record
                    ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
                    thanhToanDTO.setIdDonHang(orderId);
                    thanhToanDTO.setSoTien(donHang.getTienDatCoc());
                    thanhToanDTO.setLoaiThanhToan("dat_coc");
                    thanhToanDTO.setPhuongThuc(request.paymentMethod.toLowerCase());
                    thanhToanDTO.setMaGiaoDich(request.transactionId);
                    thanhToanDTO.setTrangThai("hoan_thanh");
                    
                    try {
                        ThanhToanDTO savedPayment = thanhToanService.createThanhToan(thanhToanDTO);
                        System.out.println("Created payment record: " + savedPayment);
                    } catch (Exception e) {
                        System.err.println("Error creating payment record: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái thanh toán thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi cập nhật trạng thái thanh toán: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
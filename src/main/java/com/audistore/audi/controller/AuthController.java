package com.audistore.audi.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.audistore.audi.dto.DangKyRequest;
import com.audistore.audi.dto.DangNhapRequest;
import com.audistore.audi.dto.JwtResponse;
import com.audistore.audi.dto.QuenMatKhauRequest;
import com.audistore.audi.dto.ResetMatKhauRequest;
import com.audistore.audi.dto.XacThucOtpRequest;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.security.jwt.JwtUtils;
import com.audistore.audi.security.service.UserDetailsImpl;
import com.audistore.audi.service.EmailService;
import com.audistore.audi.service.FirebaseService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.audistore.audi.security.service.UserDetailsServiceImpl;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    NguoiDungRepository nguoiDungRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    EmailService emailService;
    
    @Autowired
    FirebaseService firebaseService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    JavaMailSender emailSender;

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody DangNhapRequest dangNhapRequest) {
        try {
            System.out.println("=== Login Debug ===");
            System.out.println("Login attempt for: " + dangNhapRequest.getTenDangNhap());
            
            // Trước tiên, kiểm tra xem user có tồn tại và trạng thái như thế nào
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(dangNhapRequest.getTenDangNhap())
                    .orElseGet(() -> nguoiDungRepository.findBySoDienThoai(dangNhapRequest.getTenDangNhap())
                            .orElse(null));
            
            if (nguoiDung == null) {
                System.out.println("User not found: " + dangNhapRequest.getTenDangNhap());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email hoặc mật khẩu không đúng"));
            }
            
            System.out.println("User found: " + nguoiDung.getEmail());
            System.out.println("User status: " + nguoiDung.getTrangThai());
            
            // Kiểm tra trạng thái trước khi authenticate
            if (!nguoiDung.getTrangThai()) {
                System.out.println("User is disabled: " + nguoiDung.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên."));
            }
            
            System.out.println("User is enabled, proceeding with authentication");
            
            // Kiểm tra đăng nhập
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dangNhapRequest.getTenDangNhap(), dangNhapRequest.getMatKhau()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            System.out.println("Authentication successful for: " + userDetails.getEmail());
            System.out.println("User enabled: " + userDetails.isEnabled());
            
            String vaiTro = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(item -> item.getAuthority().replace("ROLE_", ""))
                    .orElse("");

            return ResponseEntity.ok(new JwtResponse(jwt, 
                                                     userDetails.getId(), 
                                                     userDetails.getEmail(), 
                                                     vaiTro,
                                                     userDetails.getAvatar()));
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email hoặc mật khẩu không đúng"));
        }
    }

    @PostMapping(value = "/dang-ky", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
        @RequestParam("avatar") MultipartFile avatar,
        @RequestParam("email") String email,
        @RequestParam("matKhau") String matKhau,
        @RequestParam("ho") String ho,
        @RequestParam("ten") String ten,
        @RequestParam("soDienThoai") String soDienThoai,
        @RequestParam("diaChi") String diaChi,
        @RequestParam("thanhPho") String thanhPho,
        @RequestParam("tinh") String tinh,
        @RequestParam(value = "maBuuDien", required = false) String maBuuDien,
        @RequestParam(value = "quocGia", required = false) String quocGia
    ) {
        Map<String, String> response = new HashMap<>();
        
        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungRepository.existsByEmail(email)) {
            response.put("message", "Email đã được sử dụng!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra số điện thoại đã tồn tại chưa (nếu có)
        if (soDienThoai != null && !soDienThoai.isEmpty()
                && nguoiDungRepository.existsBySoDienThoai(soDienThoai)) {
            response.put("message", "Số điện thoại đã được sử dụng!");
            return ResponseEntity.badRequest().body(response);
        }

        // Tạo tài khoản người dùng mới
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail(email);
        nguoiDung.setMatKhauMaHoa(encoder.encode(matKhau));
        nguoiDung.setHo(ho);
        nguoiDung.setTen(ten);
        nguoiDung.setSoDienThoai(soDienThoai);
        nguoiDung.setDiaChi(diaChi);
        nguoiDung.setThanhPho(thanhPho);
        nguoiDung.setTinh(tinh);
        nguoiDung.setMaBuuDien(maBuuDien);
        nguoiDung.setQuocGia(quocGia != null && !quocGia.isEmpty() ? quocGia : "Việt Nam");
        nguoiDung.setVaiTro(NguoiDung.VaiTro.khach_hang); // Mặc định là khách hàng
        
        // Lưu file vào thư mục uploads/avatar_user/
        String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/images/avatar_user/" + fileName);
        try {
            Files.copy(avatar.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            nguoiDung.setAvatar(fileName);
        } catch (Exception e) {
            response.put("message", "Lỗi khi lưu file avatar: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        nguoiDungRepository.save(nguoiDung);

        response.put("message", "Người dùng đã được đăng ký thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quen-mat-khau")
    public ResponseEntity<?> quenMatKhau(@Valid @RequestBody QuenMatKhauRequest request) {
        Map<String, String> response = new HashMap<>();
        
        String emailOrPhone = request.getEmailOrPhone();
        String method = request.getMethod();
        
        Optional<NguoiDung> nguoiDungOpt;
        
        // Kiểm tra xem input là email hay số điện thoại
        if (emailOrPhone.contains("@")) {
            nguoiDungOpt = nguoiDungRepository.findByEmail(emailOrPhone);
        } else {
            nguoiDungOpt = nguoiDungRepository.findBySoDienThoai(emailOrPhone);
        }
        
        if (nguoiDungOpt.isEmpty()) {
            response.put("message", "Không tìm thấy tài khoản với thông tin đã cung cấp!");
            return ResponseEntity.badRequest().body(response);
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        if ("email".equals(method)) {
            // Gửi email với mã reset
            String resetToken = generateResetToken();
            nguoiDung.setResetToken(resetToken);
            nguoiDung.setResetTokenExpiry(System.currentTimeMillis() + (15 * 60 * 1000)); // 15 phút
            nguoiDungRepository.save(nguoiDung);
            
            try {
                emailService.guiEmailQuenMatKhau(nguoiDung.getEmail(), resetToken);
                response.put("message", "Email đặt lại mật khẩu đã được gửi!");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("message", "Không thể gửi email. Vui lòng thử lại sau!");
                return ResponseEntity.badRequest().body(response);
            }
        } else if ("sms".equals(method)) {
            // Kiểm tra Firebase có khả dụng không
            if (!firebaseService.isFirebaseAvailable()) {
                response.put("message", "Tính năng SMS tạm thời không khả dụng. Vui lòng sử dụng phương thức Email.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Gửi OTP qua Firebase
            try {
                String sessionInfo = firebaseService.createSessionCookie(nguoiDung.getEmail());
                
                response.put("message", "OTP đã được gửi qua SMS!");
                response.put("sessionInfo", sessionInfo);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("message", "Không thể gửi OTP. Vui lòng thử lại sau hoặc sử dụng phương thức Email.");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("message", "Phương thức không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/reset-mat-khau")
    public ResponseEntity<?> resetMatKhau(@Valid @RequestBody ResetMatKhauRequest request) {
        Map<String, String> response = new HashMap<>();
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByResetToken(request.getToken());
        
        if (nguoiDungOpt.isEmpty()) {
            response.put("message", "Token không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra token có hết hạn chưa
        if (System.currentTimeMillis() > nguoiDung.getResetTokenExpiry()) {
            response.put("message", "Token đã hết hạn!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Cập nhật mật khẩu mới
        nguoiDung.setMatKhauMaHoa(encoder.encode(request.getMatKhauMoi()));
        nguoiDung.setResetToken(null);
        nguoiDung.setResetTokenExpiry(null);
        nguoiDungRepository.save(nguoiDung);
        
        response.put("message", "Mật khẩu đã được đặt lại thành công!");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/xac-thuc-otp")
    public ResponseEntity<?> xacThucOtp(@Valid @RequestBody XacThucOtpRequest request) {
        Map<String, String> response = new HashMap<>();
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findBySoDienThoai(request.getSoDienThoai());
        
        if (nguoiDungOpt.isEmpty()) {
            response.put("message", "Số điện thoại không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra OTP
        if (firebaseService.verifyOtp(request.getSoDienThoai(), request.getOtp())) {
            nguoiDung.setMatKhauMaHoa(encoder.encode(request.getMatKhauMoi()));
            nguoiDungRepository.save(nguoiDung);
            
            response.put("message", "Mật khẩu đã được đặt lại thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Mã OTP không đúng!");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private String generateResetToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Map<String, Object> response = new HashMap<>();
            response.put("id", userDetails.getId());
            response.put("email", userDetails.getEmail());
            response.put("authorities", userDetails.getAuthorities());
            response.put("enabled", userDetails.isEnabled());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    @GetMapping("/debug/current-user")
    public ResponseEntity<?> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Map<String, Object> response = new HashMap<>();
            response.put("id", userDetails.getId());
            response.put("email", userDetails.getEmail());
            response.put("authorities", userDetails.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
            response.put("enabled", userDetails.isEnabled());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    @PostMapping("/clear-cache/{email}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<?> clearUserCache(@PathVariable String email) {
        try {
            System.out.println("Clearing cache for user: " + email);
            // Clear Spring Security cache
            userDetailsService.clearUserCache(email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error clearing cache: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi clear cache"));
        }
    }

    @GetMapping("/check-status/{email}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<?> checkUserStatus(@PathVariable String email) {
        try {
            System.out.println("Checking status for user: " + email);
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                    .orElseGet(() -> nguoiDungRepository.findBySoDienThoai(email)
                            .orElse(null));
            
            if (nguoiDung == null) {
                System.out.println("User not found: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy người dùng"));
            }
            
            System.out.println("User status: " + nguoiDung.getTrangThai());
            return ResponseEntity.ok(Map.of(
                "email", nguoiDung.getEmail(),
                "trangThai", nguoiDung.getTrangThai(),
                "message", nguoiDung.getTrangThai() ? "Tài khoản hoạt động" : "Tài khoản đã bị vô hiệu hóa"
            ));
        } catch (Exception e) {
            System.out.println("Error checking status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi kiểm tra trạng thái"));
        }
    }

    @PostMapping("/refresh-user/{email}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<?> refreshUserData(@PathVariable String email) {
        try {
            System.out.println("Refreshing user data for: " + email);
            
            // Clear cache
            userDetailsService.clearUserCache(email);
            
            // Get fresh user data from database
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                    .orElseGet(() -> nguoiDungRepository.findBySoDienThoai(email)
                            .orElse(null));
            
            if (nguoiDung == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy người dùng"));
            }
            
            return ResponseEntity.ok(Map.of(
                "email", nguoiDung.getEmail(),
                "trangThai", nguoiDung.getTrangThai(),
                "message", "User data refreshed successfully"
            ));
        } catch (Exception e) {
            System.out.println("Error refreshing user data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi refresh user data"));
        }
    }

    @PostMapping("/unlock-user/{email}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<?> unlockUserAndResetPassword(@PathVariable String email) {
        try {
            System.out.println("Unlocking user and resetting password for: " + email);
            
            // Tìm user theo email
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
            if (nguoiDungOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy người dùng với email: " + email));
            }
            
            NguoiDung nguoiDung = nguoiDungOpt.get();
            
            // Kiểm tra xem user có bị khóa không
            if (nguoiDung.getTrangThai()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Tài khoản đã hoạt động, không cần mở khóa"));
            }
            
            // Tạo password mới 6 ký tự (chỉ chữ thường và số)
            String newPassword = generateRandomPassword();
            System.out.println("Generated new password: " + newPassword);
            
            // Cập nhật password và trạng thái
            nguoiDung.setMatKhauMaHoa(encoder.encode(newPassword));
            nguoiDung.setTrangThai(true);
            nguoiDungRepository.save(nguoiDung);
            
            // Gửi email với password mới - sử dụng method có sẵn
            String emailContent = String.format(
                "Xin chào %s %s,\n\n" +
                "Tài khoản của bạn đã được mở khóa thành công!\n\n" +
                "Thông tin đăng nhập mới:\n" +
                "Email: %s\n" +
                "Mật khẩu mới: %s\n\n" +
                "⚠️ LƯU Ý QUAN TRỌNG:\n" +
                "1. Vui lòng đăng nhập ngay với mật khẩu mới\n" +
                "2. Sau khi đăng nhập, vào trang 'Tài khoản của tôi' để đổi mật khẩu\n" +
                "3. Đổi mật khẩu để bảo mật tài khoản của bạn\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ hỗ trợ Audi",
                nguoiDung.getHo(), nguoiDung.getTen(), 
                nguoiDung.getEmail(), newPassword
            );
            
            // Sử dụng SimpleMailMessage trực tiếp
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@audi-vietnam.com");
            message.setTo(nguoiDung.getEmail());
            message.setSubject("Tài khoản đã được mở khóa - Audi");
            message.setText(emailContent);
            
            // Gửi email
            try {
                emailSender.send(message);
                System.out.println("Email sent successfully to: " + nguoiDung.getEmail());
            } catch (Exception emailError) {
                System.out.println("Error sending email: " + emailError.getMessage());
                // Vẫn tiếp tục unlock user dù email có lỗi
            }
            
            // Clear cache
            userDetailsService.clearUserCache(email);
            
            System.out.println("User unlocked successfully: " + email);
            
            return ResponseEntity.ok(Map.of(
                "message", "Tài khoản đã được mở khóa thành công! Mật khẩu mới đã được gửi qua email.",
                "email", nguoiDung.getEmail(),
                "trangThai", true,
                "newPassword", newPassword // Trả về password để admin có thể thông báo
            ));
            
        } catch (Exception e) {
            System.out.println("Error unlocking user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi mở khóa tài khoản: " + e.getMessage()));
        }
    }
    
    @PostMapping("/lock-user/{email}")
    public ResponseEntity<?> lockUser(@PathVariable String email) {
        try {
            System.out.println("Locking user: " + email);
            
            // Tìm user theo email
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
            if (nguoiDungOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy người dùng với email: " + email));
            }
            
            NguoiDung nguoiDung = nguoiDungOpt.get();
            
            // Kiểm tra nếu user đã bị khóa
            if (!nguoiDung.getTrangThai()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tài khoản đã bị khóa"));
            }
            
            // Khóa user
            nguoiDung.setTrangThai(false);
            nguoiDungRepository.save(nguoiDung);
            
            // Clear cache
            clearUserCache(email);
            
            return ResponseEntity.ok(Map.of("message", "Đã khóa tài khoản thành công"));
            
        } catch (Exception e) {
            System.err.println("Error locking user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Có lỗi xảy ra khi khóa tài khoản"));
        }
    }

    // Tạo password ngẫu nhiên 6 ký tự (chỉ chữ thường và số)
    private String generateRandomPassword() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
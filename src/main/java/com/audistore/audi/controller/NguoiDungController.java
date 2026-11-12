package com.audistore.audi.controller;

import com.audistore.audi.dto.NguoiDungDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/nguoi-dung")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public NguoiDungController(NguoiDungService nguoiDungService, NguoiDungRepository nguoiDungRepository, PasswordEncoder encoder) {
        this.nguoiDungService = nguoiDungService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.encoder = encoder;
    }

    @GetMapping
    public ResponseEntity<List<NguoiDungDTO>> getAllNguoiDung() {
        return ResponseEntity.ok(nguoiDungService.getAllNguoiDung());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> getNguoiDungById(@PathVariable Long id) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        NguoiDung currentUser = nguoiDungRepository.findByEmail(currentUserEmail).orElse(null);
        
        // Kiểm tra xem người dùng hiện tại có phải là admin hoặc đang xem chính thông tin của mình
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        
        if (currentUser != null && (isAdmin || currentUser.getId().equals(id))) {
            return ResponseEntity.ok(nguoiDungService.getNguoiDungById(id));
        } else {
            throw new AccessDeniedException("Bạn không có quyền xem thông tin của người dùng khác");
        }
    }

    @PostMapping
    public ResponseEntity<NguoiDungDTO> createNguoiDung(@RequestBody NguoiDungDTO nguoiDungDTO) {
        return ResponseEntity.ok(nguoiDungService.createNguoiDung(nguoiDungDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> updateNguoiDung(@PathVariable Long id, @RequestBody NguoiDungDTO nguoiDungDTO) {
        // Kiểm tra quyền tương tự như trong getNguoiDungById
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        NguoiDung currentUser = nguoiDungRepository.findByEmail(currentUserEmail).orElse(null);
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        
        if (currentUser != null && (isAdmin || currentUser.getId().equals(id))) {
            return ResponseEntity.ok(nguoiDungService.updateNguoiDung(id, nguoiDungDTO));
        } else {
            throw new AccessDeniedException("Bạn không có quyền cập nhật thông tin của người dùng khác");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNguoiDung(@PathVariable Long id) {
        // Chỉ admin mới có quyền xóa người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        
        if (isAdmin) {
            nguoiDungService.deleteNguoiDung(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccessDeniedException("Bạn không có quyền xóa người dùng");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        // Lấy thông tin người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng hiện tại"));
        
        // Kiểm tra trạng thái tài khoản
        if (!nguoiDung.getTrangThai()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Tài khoản đã bị khóa bởi quản trị viên");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
        // Get user data
        NguoiDungDTO userDTO = nguoiDungService.getNguoiDungById(nguoiDung.getId());
        
        // Set a masked password for display
        userDTO.setMatKhau("********");
        
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody NguoiDungDTO profileDTO) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng hiện tại"));
            
            // Cập nhật thông tin profile (không cho phép thay đổi email, vai trò, trạng thái)
            if (profileDTO.getHo() != null) nguoiDung.setHo(profileDTO.getHo());
            if (profileDTO.getTen() != null) nguoiDung.setTen(profileDTO.getTen());
            if (profileDTO.getSoDienThoai() != null) nguoiDung.setSoDienThoai(profileDTO.getSoDienThoai());
            if (profileDTO.getDiaChi() != null) nguoiDung.setDiaChi(profileDTO.getDiaChi());
            if (profileDTO.getThanhPho() != null) nguoiDung.setThanhPho(profileDTO.getThanhPho());
            if (profileDTO.getTinh() != null) nguoiDung.setTinh(profileDTO.getTinh());
            if (profileDTO.getMaBuuDien() != null) nguoiDung.setMaBuuDien(profileDTO.getMaBuuDien());
            if (profileDTO.getQuocGia() != null) nguoiDung.setQuocGia(profileDTO.getQuocGia());
            
            nguoiDungRepository.save(nguoiDung);
            
            response.put("message", "Cập nhật thông tin thành công!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng hiện tại"));
            
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");
            
            // Kiểm tra mật khẩu hiện tại
            if (!encoder.matches(currentPassword, nguoiDung.getMatKhauMaHoa())) {
                response.put("message", "Mật khẩu hiện tại không đúng!");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Kiểm tra mật khẩu mới
            if (newPassword == null || newPassword.length() < 6) {
                response.put("message", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Kiểm tra xác nhận mật khẩu
            if (!newPassword.equals(confirmPassword)) {
                response.put("message", "Mật khẩu xác nhận không khớp!");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Cập nhật mật khẩu mới
            nguoiDung.setMatKhauMaHoa(encoder.encode(newPassword));
            nguoiDungRepository.save(nguoiDung);
            
            response.put("message", "Đổi mật khẩu thành công!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
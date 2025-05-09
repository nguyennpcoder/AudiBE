package com.audistore.audi.controller;

import com.audistore.audi.dto.NguoiDungDTO;
import com.audistore.audi.dto.NguoiDungQuyenDTO;
import com.audistore.audi.dto.NhomQuyenDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/quan-ly-nhan-vien")
@PreAuthorize("hasRole('QUAN_TRI')")
public class QuanLyNhanVienController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public ResponseEntity<List<NguoiDungDTO>> getAllNhanVien() {
        List<NguoiDungDTO> nhanVien = nguoiDungService.getAllNguoiDung().stream()
                .filter(nd -> nd.getVaiTro() != null && !"khach_hang".equals(nd.getVaiTro()))
                .peek(this::setMatKhauGoc)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nhanVien);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> getNguoiDungById(@PathVariable Long id) {
        NguoiDungDTO nguoiDungDTO = nguoiDungService.getNguoiDungById(id);
        
        // Nếu người dùng hiện tại là admin, thêm mật khẩu gốc vào DTO
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"))) {
            String matKhauGoc = nguoiDungService.getMatKhauGoc(id);
            nguoiDungDTO.setMatKhau(matKhauGoc);
        }
        
        return ResponseEntity.ok(nguoiDungDTO);
    }

    @GetMapping("/{id}/nhom-quyen")
    public ResponseEntity<Set<NhomQuyenDTO>> getNhomQuyenByNhanVienId(@PathVariable Long id) {
        return ResponseEntity.ok(nguoiDungService.getNhomQuyenByNguoiDungId(id));
    }

    @PostMapping
    public ResponseEntity<NguoiDungDTO> createNhanVien(@RequestBody NguoiDungDTO nguoiDungDTO) {
        if (nguoiDungDTO.getVaiTro() != null && "khach_hang".equals(nguoiDungDTO.getVaiTro())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(nguoiDungService.createNguoiDung(nguoiDungDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> updateNhanVien(@PathVariable Long id, @RequestBody NguoiDungDTO nguoiDungDTO) {
        NguoiDungDTO existingUser = nguoiDungService.getNguoiDungById(id);
        if (existingUser.getVaiTro() != null && "khach_hang".equals(existingUser.getVaiTro())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(nguoiDungService.updateNguoiDung(id, nguoiDungDTO));
    }

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<Void> toggleTrangThai(@PathVariable Long id) {
        NguoiDungDTO existingUser = nguoiDungService.getNguoiDungById(id);
        if (existingUser.getVaiTro() != null && "khach_hang".equals(existingUser.getVaiTro())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        existingUser.setTrangThai(!existingUser.getTrangThai());
        nguoiDungService.updateNguoiDung(id, existingUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phan-quyen")
    public ResponseEntity<Void> assignRoles(@RequestBody NguoiDungQuyenDTO nguoiDungQuyenDTO) {
        nguoiDungService.giaoPhanQuyen(nguoiDungQuyenDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dat-lai-mat-khau")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        // Tạo mật khẩu ngẫu nhiên hoặc sử dụng mật khẩu đã cung cấp
        String newPassword = request.getOrDefault("matKhau", generateRandomPassword());
        
        // Đặt lại mật khẩu
        nguoiDungService.resetPassword(id, newPassword);
        
        // Chỉ trả về mật khẩu mới trong response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã đặt lại mật khẩu thành công");
        response.put("matKhauMoi", newPassword);
        
        return ResponseEntity.ok(response);
    }
    
    private String generateRandomPassword() {
        // Tạo mật khẩu ngẫu nhiên 8 ký tự
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private void setMatKhauGoc(NguoiDungDTO dto) {
        if (dto != null && dto.getId() != null) {
            // Lấy mật khẩu gốc đã giải mã
            String matKhauGoc = nguoiDungService.getMatKhauGoc(dto.getId());
            dto.setMatKhau(matKhauGoc);
        }
    }
}
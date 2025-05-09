package com.audistore.audi.controller;

import com.audistore.audi.dto.DangKyNhanTinDTO;
import com.audistore.audi.service.DangKyNhanTinService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dang-ky-nhan-tin")
public class DangKyNhanTinController {

    private final DangKyNhanTinService dangKyNhanTinService;

    @Autowired
    public DangKyNhanTinController(DangKyNhanTinService dangKyNhanTinService) {
        this.dangKyNhanTinService = dangKyNhanTinService;
    }

    // Endpoint cho người dùng đăng ký nhận tin (public)
    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKyNhanTin(@Valid @RequestBody DangKyNhanTinDTO dangKyNhanTinDTO) {
        try {
            DangKyNhanTinDTO createdDangKy = dangKyNhanTinService.createDangKyNhanTin(dangKyNhanTinDTO);
            Map<String, String> response = new HashMap<>();
            
            // Kiểm tra xem có phải đăng ký lại hay không
            if (Boolean.TRUE.equals(createdDangKy.getResubscribed())) {
                response.put("message", "Đã đăng ký nhận tin lại thành công");
            } else {
                response.put("message", "Đăng ký nhận tin thành công");
            }
            
            response.put("email", createdDangKy.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint cho người dùng hủy đăng ký (public)
    @PostMapping("/huy-dang-ky")
    public ResponseEntity<?> huyDangKy(@RequestParam String email) {
        try {
            DangKyNhanTinDTO updatedDangKy = dangKyNhanTinService.unsubscribe(email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã hủy đăng ký nhận tin thành công");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Các endpoint quản lý dành cho admin
    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<List<DangKyNhanTinDTO>> getAllDangKyNhanTin() {
        return ResponseEntity.ok(dangKyNhanTinService.getAllDangKyNhanTin());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<List<DangKyNhanTinDTO>> getActiveDangKyNhanTin() {
        return ResponseEntity.ok(dangKyNhanTinService.getDangKyNhanTinActive());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<DangKyNhanTinDTO> getDangKyNhanTinById(@PathVariable Long id) {
        return ResponseEntity.ok(dangKyNhanTinService.getDangKyNhanTinById(id));
    }

    @GetMapping("/email")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<?> getDangKyNhanTinByEmail(@RequestParam String email) {
        DangKyNhanTinDTO dangKyNhanTinDTO = dangKyNhanTinService.getDangKyNhanTinByEmail(email);
        if (dangKyNhanTinDTO != null) {
            return ResponseEntity.ok(dangKyNhanTinDTO);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email chưa đăng ký nhận tin");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<DangKyNhanTinDTO> updateDangKyNhanTin(@PathVariable Long id, @Valid @RequestBody DangKyNhanTinDTO dangKyNhanTinDTO) {
        return ResponseEntity.ok(dangKyNhanTinService.updateDangKyNhanTin(id, dangKyNhanTinDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Map<String, String>> deleteDangKyNhanTin(@PathVariable Long id) {
        dangKyNhanTinService.deleteDangKyNhanTin(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa đăng ký nhận tin thành công");
        return ResponseEntity.ok(response);
    }
}
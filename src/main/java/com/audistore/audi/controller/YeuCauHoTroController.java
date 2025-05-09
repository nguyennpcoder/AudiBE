package com.audistore.audi.controller;

import com.audistore.audi.dto.YeuCauHoTroDTO;
import com.audistore.audi.service.YeuCauHoTroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ho-tro")
public class YeuCauHoTroController {

    @Autowired
    private YeuCauHoTroService yeuCauHoTroService;

    @PostMapping
    public ResponseEntity<YeuCauHoTroDTO> taoYeuCauHoTro(@Valid @RequestBody YeuCauHoTroDTO dto) {
        YeuCauHoTroDTO createdYeuCau = yeuCauHoTroService.taoYeuCauHoTro(dto);
        return new ResponseEntity<>(createdYeuCau, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<YeuCauHoTroDTO> getYeuCauById(@PathVariable Long id) {
        YeuCauHoTroDTO yeuCauDTO = yeuCauHoTroService.getYeuCauById(id);
        return ResponseEntity.ok(yeuCauDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Object>> getAllYeuCau(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<YeuCauHoTroDTO> yeuCauPage = yeuCauHoTroService.getAllYeuCau(page, size, sortBy, sortDir);
        
        Map<String, Object> response = new HashMap<>();
        response.put("yeuCau", yeuCauPage.getContent());
        response.put("currentPage", yeuCauPage.getNumber());
        response.put("totalItems", yeuCauPage.getTotalElements());
        response.put("totalPages", yeuCauPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<Map<String, Object>> getYeuCauByNguoiDung(
            @PathVariable Long idNguoiDung,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<YeuCauHoTroDTO> yeuCauPage = yeuCauHoTroService.getYeuCauByNguoiDung(idNguoiDung, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("yeuCau", yeuCauPage.getContent());
        response.put("currentPage", yeuCauPage.getNumber());
        response.put("totalItems", yeuCauPage.getTotalElements());
        response.put("totalPages", yeuCauPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trang-thai/{trangThai}")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Object>> getYeuCauByTrangThai(
            @PathVariable String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<YeuCauHoTroDTO> yeuCauPage = yeuCauHoTroService.getYeuCauByTrangThai(trangThai, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("yeuCau", yeuCauPage.getContent());
        response.put("currentPage", yeuCauPage.getNumber());
        response.put("totalItems", yeuCauPage.getTotalElements());
        response.put("totalPages", yeuCauPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nguoi-phu-trach/{idNguoiPhuTrach}")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Object>> getYeuCauByNguoiPhuTrach(
            @PathVariable Long idNguoiPhuTrach,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<YeuCauHoTroDTO> yeuCauPage = yeuCauHoTroService.getYeuCauByNguoiPhuTrach(idNguoiPhuTrach, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("yeuCau", yeuCauPage.getContent());
        response.put("currentPage", yeuCauPage.getNumber());
        response.put("totalItems", yeuCauPage.getTotalElements());
        response.put("totalPages", yeuCauPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tim-kiem")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Object>> timKiemYeuCau(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<YeuCauHoTroDTO> yeuCauPage = yeuCauHoTroService.timKiemYeuCau(keyword, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("yeuCau", yeuCauPage.getContent());
        response.put("currentPage", yeuCauPage.getNumber());
        response.put("totalItems", yeuCauPage.getTotalElements());
        response.put("totalPages", yeuCauPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<YeuCauHoTroDTO> capNhatTrangThai(
            @PathVariable Long id,
            @RequestParam String trangThai,
            Authentication authentication) {
        
        Long idNguoiCapNhat = Long.parseLong(authentication.getName());
        YeuCauHoTroDTO updatedYeuCau = yeuCauHoTroService.capNhatTrangThai(id, trangThai, idNguoiCapNhat);
        return ResponseEntity.ok(updatedYeuCau);
    }

    @PatchMapping("/{id}/uu-tien")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<YeuCauHoTroDTO> capNhatUuTien(
            @PathVariable Long id,
            @RequestParam String mucDoUuTien) {
        
        YeuCauHoTroDTO updatedYeuCau = yeuCauHoTroService.capNhatUuTien(id, mucDoUuTien);
        return ResponseEntity.ok(updatedYeuCau);
    }

    @PatchMapping("/{id}/phan-cong")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<YeuCauHoTroDTO> phanCongNguoiPhuTrach(
            @PathVariable Long id,
            @RequestParam Long idNguoiPhuTrach) {
        
        YeuCauHoTroDTO updatedYeuCau = yeuCauHoTroService.phanCongNguoiPhuTrach(id, idNguoiPhuTrach);
        return ResponseEntity.ok(updatedYeuCau);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> xoaYeuCau(@PathVariable Long id) {
        yeuCauHoTroService.xoaYeuCau(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/thong-ke")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Long>> thongKeYeuCauTheoTrangThai() {
        Map<String, Long> stats = yeuCauHoTroService.thongKeYeuCauTheoTrangThai();
        return ResponseEntity.ok(stats);
    }
}
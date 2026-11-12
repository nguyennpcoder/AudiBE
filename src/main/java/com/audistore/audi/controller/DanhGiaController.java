package com.audistore.audi.controller;

import com.audistore.audi.dto.DanhGiaDTO;
import com.audistore.audi.security.service.UserDetailsImpl;
import com.audistore.audi.service.DanhGiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/danh-gia")
public class DanhGiaController {

    @Autowired
    private DanhGiaService danhGiaService;

    @PostMapping
    public ResponseEntity<DanhGiaDTO> themDanhGia(@Valid @RequestBody DanhGiaDTO danhGiaDTO) {
        return new ResponseEntity<>(danhGiaService.themDanhGia(danhGiaDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DanhGiaDTO> getDanhGiaById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(danhGiaService.getDanhGiaById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Object>> getAllDanhGia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<DanhGiaDTO> danhGiaPage = danhGiaService.getAllDanhGia(page, size, sortBy, sortDir);
        
        Map<String, Object> response = new HashMap<>();
        response.put("danhGia", danhGiaPage.getContent());
        response.put("currentPage", danhGiaPage.getNumber());
        response.put("totalItems", danhGiaPage.getTotalElements());
        response.put("totalPages", danhGiaPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mau-xe/{idMauXe}")
    public ResponseEntity<Map<String, Object>> getDanhGiaByMauXe(
            @PathVariable(name = "idMauXe") Long idMauXe,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Page<DanhGiaDTO> danhGiaPage;
        double trungBinhSao = danhGiaService.tinhTrungBinhSaoMauXe(idMauXe);
        
        // Nếu có xác thực, lấy cả đánh giá đang chờ duyệt của người dùng hiện tại
        if (authentication != null && authentication.isAuthenticated()) {
            Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
            danhGiaPage = danhGiaService.getDanhGiaByMauXeForDisplay(idMauXe, currentUserId, page, size);
        } else {
            // Nếu không có xác thực, chỉ lấy đánh giá đã duyệt
            danhGiaPage = danhGiaService.getDanhGiaByMauXe(idMauXe, page, size);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("danhGia", danhGiaPage.getContent());
        response.put("currentPage", danhGiaPage.getNumber());
        response.put("totalItems", danhGiaPage.getTotalElements());
        response.put("totalPages", danhGiaPage.getTotalPages());
        response.put("trungBinhSao", trungBinhSao);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<Map<String, Object>> getDanhGiaByNguoiDung(
            @PathVariable(name = "idNguoiDung") Long idNguoiDung,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<DanhGiaDTO> danhGiaPage = danhGiaService.getDanhGiaByNguoiDung(idNguoiDung, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("danhGia", danhGiaPage.getContent());
        response.put("currentPage", danhGiaPage.getNumber());
        response.put("totalItems", danhGiaPage.getTotalElements());
        response.put("totalPages", danhGiaPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cho-duyet")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<Map<String, Object>> getDanhGiaChoDuyet(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<DanhGiaDTO> danhGiaPage = danhGiaService.getDanhGiaChoDuyet(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("danhGia", danhGiaPage.getContent());
        response.put("currentPage", danhGiaPage.getNumber());
        response.put("totalItems", danhGiaPage.getTotalElements());
        response.put("totalPages", danhGiaPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DanhGiaDTO> capNhatDanhGia(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody DanhGiaDTO danhGiaDTO) {
        
        return ResponseEntity.ok(danhGiaService.capNhatDanhGia(id, danhGiaDTO));
    }

    @PatchMapping("/{id}/duyet")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'HO_TRO')")
    public ResponseEntity<DanhGiaDTO> duyetDanhGia(
            @PathVariable(name = "id") Long id,
            @RequestParam boolean approve) {
        
        return ResponseEntity.ok(danhGiaService.duyetDanhGia(id, approve));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> xoaDanhGia(@PathVariable(name = "id") Long id) {
        danhGiaService.xoaDanhGia(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/mau-xe/{idMauXe}/trung-binh")
    public ResponseEntity<Map<String, Object>> getTrungBinhSaoMauXe(
            @PathVariable(name = "idMauXe") Long idMauXe,
            Authentication authentication) {
        
        double trungBinhSao;
        
        // Nếu có xác thực, tính trung bình bao gồm cả đánh giá đang chờ duyệt của người dùng hiện tại
        if (authentication != null && authentication.isAuthenticated()) {
            Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
            trungBinhSao = danhGiaService.tinhTrungBinhSaoMauXeForDisplay(idMauXe, currentUserId);
        } else {
            // Nếu không có xác thực, chỉ tính trung bình từ đánh giá đã duyệt
            trungBinhSao = danhGiaService.tinhTrungBinhSaoMauXe(idMauXe);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("idMauXe", idMauXe);
        response.put("trungBinhSao", trungBinhSao);
        
        return ResponseEntity.ok(response);
    }
}
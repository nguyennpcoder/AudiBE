package com.audistore.audi.controller;

import com.audistore.audi.dto.NhatKyHoatDongDTO;
import com.audistore.audi.dto.ThongKeHoatDongDTO;
import com.audistore.audi.service.NhatKyHoatDongService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nhat-ky")
@PreAuthorize("hasRole('QUAN_TRI')")
public class NhatKyHoatDongController {

    private final NhatKyHoatDongService nhatKyHoatDongService;

    @Autowired
    public NhatKyHoatDongController(NhatKyHoatDongService nhatKyHoatDongService) {
        this.nhatKyHoatDongService = nhatKyHoatDongService;
    }

    // Thêm endpoint test-log
    @PostMapping("/test-log")
    public ResponseEntity<?> testLogActivity(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        String loaiHoatDong = (String) payload.get("loaiHoatDong");
        if (loaiHoatDong == null || loaiHoatDong.isEmpty()) {
            loaiHoatDong = "test_log";
        }
        
        Map<String, Object> chiTiet = new HashMap<>();
        chiTiet.put("testPayload", payload);
        chiTiet.put("timestamp", System.currentTimeMillis());
        
        nhatKyHoatDongService.logHoatDong(loaiHoatDong, chiTiet, request);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã log hoạt động thành công");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<NhatKyHoatDongDTO>> getAllNhatKyHoatDong(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        return ResponseEntity.ok(nhatKyHoatDongService.getAllNhatKyHoatDong(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhatKyHoatDongDTO> getNhatKyHoatDongById(@PathVariable Long id) {
        return ResponseEntity.ok(nhatKyHoatDongService.getNhatKyHoatDongById(id));
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<Page<NhatKyHoatDongDTO>> getNhatKyByNguoiDung(
            @PathVariable Long idNguoiDung,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(nhatKyHoatDongService.getNhatKyByNguoiDung(idNguoiDung, page, size));
    }

    @GetMapping("/loai/{loaiHoatDong}")
    public ResponseEntity<Page<NhatKyHoatDongDTO>> getNhatKyByLoaiHoatDong(
            @PathVariable String loaiHoatDong,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(nhatKyHoatDongService.getNhatKyByLoaiHoatDong(loaiHoatDong, page, size));
    }

    @GetMapping("/ngay")
    public ResponseEntity<Page<NhatKyHoatDongDTO>> getNhatKyByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(nhatKyHoatDongService.getNhatKyByDateRange(tuNgay, denNgay, page, size));
    }

    @GetMapping("/loai-hoat-dong")
    public ResponseEntity<List<String>> getLoaiHoatDongList() {
        return ResponseEntity.ok(nhatKyHoatDongService.getLoaiHoatDongList());
    }

    @GetMapping("/thong-ke")
    public ResponseEntity<ThongKeHoatDongDTO> getThongKeHoatDong() {
        return ResponseEntity.ok(nhatKyHoatDongService.getThongKeHoatDong());
    }
}
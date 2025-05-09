package com.audistore.audi.controller;

import com.audistore.audi.dto.DanhSachYeuThichDTO;
import com.audistore.audi.service.DanhSachYeuThichService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/yeu-thich")
public class DanhSachYeuThichController {

    @Autowired
    private DanhSachYeuThichService danhSachYeuThichService;

    @PostMapping
    public ResponseEntity<DanhSachYeuThichDTO> themVaoDanhSachYeuThich(
            Authentication authentication,
            @RequestParam Long idMauXe) {
        
        Long idNguoiDung = Long.parseLong(authentication.getName());
        DanhSachYeuThichDTO dto = danhSachYeuThichService.themVaoDanhSachYeuThich(idNguoiDung, idMauXe);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDanhSachYeuThich(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long idNguoiDung = Long.parseLong(authentication.getName());
        Page<DanhSachYeuThichDTO> yeuThichPage = 
                danhSachYeuThichService.getDanhSachYeuThichByNguoiDung(idNguoiDung, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("yeuThich", yeuThichPage.getContent());
        response.put("currentPage", yeuThichPage.getNumber());
        response.put("totalItems", yeuThichPage.getTotalElements());
        response.put("totalPages", yeuThichPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> kiemTraMauXeTrongDanhSachYeuThich(
            Authentication authentication,
            @RequestParam Long idMauXe) {
        
        Long idNguoiDung = Long.parseLong(authentication.getName());
        boolean exists = danhSachYeuThichService.kiemTraMauXeTrongDanhSachYeuThich(idNguoiDung, idMauXe);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/mau-xe/{idMauXe}")
    public ResponseEntity<Void> xoaKhoiDanhSachYeuThich(
            Authentication authentication,
            @PathVariable Long idMauXe) {
        
        Long idNguoiDung = Long.parseLong(authentication.getName());
        danhSachYeuThichService.xoaKhoiDanhSachYeuThich(idNguoiDung, idMauXe);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaDanhSachYeuThich(
            Authentication authentication,
            @PathVariable Long id) {
        
        Long idNguoiDung = Long.parseLong(authentication.getName());
        danhSachYeuThichService.xoaDanhSachYeuThich(id, idNguoiDung);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/thong-ke")
    public ResponseEntity<Map<String, Object>> getThongKeDanhSachYeuThich(
            Authentication authentication) {
        
        Long idNguoiDung = Long.parseLong(authentication.getName());
        Map<String, Object> thongKe = danhSachYeuThichService.getThongKeDanhSachYeuThich(idNguoiDung);
        return ResponseEntity.ok(thongKe);
    }
}
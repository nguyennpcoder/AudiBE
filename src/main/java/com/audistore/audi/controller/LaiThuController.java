package com.audistore.audi.controller;

import com.audistore.audi.dto.LaiThuDTO;
import com.audistore.audi.service.LaiThuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lai-thu")
public class LaiThuController {

    private final LaiThuService laiThuService;

    @Autowired
    public LaiThuController(LaiThuService laiThuService) {
        this.laiThuService = laiThuService;
    }

    @GetMapping
    public ResponseEntity<List<LaiThuDTO>> getAllLaiThu() {
        return ResponseEntity.ok(laiThuService.getAllLaiThu());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaiThuDTO> getLaiThuById(@PathVariable Long id) {
        return ResponseEntity.ok(laiThuService.getLaiThuById(id));
    }
    
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<LaiThuDTO>> getLaiThuByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(laiThuService.getLaiThuByNguoiDung(idNguoiDung));
    }
    
    @GetMapping("/dai-ly/{idDaiLy}")
    public ResponseEntity<List<LaiThuDTO>> getLaiThuByDaiLy(@PathVariable Long idDaiLy) {
        return ResponseEntity.ok(laiThuService.getLaiThuByDaiLy(idDaiLy));
    }
    
    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<LaiThuDTO>> getLaiThuByTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(laiThuService.getLaiThuByTrangThai(trangThai));
    }
    
    @GetMapping("/ngay")
    public ResponseEntity<List<LaiThuDTO>> getLaiThuByNgay(
            @RequestParam Long idDaiLy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngay) {
        return ResponseEntity.ok(laiThuService.getLaiThuByNgay(idDaiLy, ngay));
    }
    
    @PostMapping
    public ResponseEntity<LaiThuDTO> createLaiThu(@Valid @RequestBody LaiThuDTO laiThuDTO) {
        return new ResponseEntity<>(laiThuService.createLaiThu(laiThuDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LaiThuDTO> updateLaiThu(@PathVariable Long id, @Valid @RequestBody LaiThuDTO laiThuDTO) {
        return ResponseEntity.ok(laiThuService.updateLaiThu(id, laiThuDTO));
    }
    
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<LaiThuDTO> updateTrangThaiLaiThu(
            @PathVariable Long id,
            @RequestParam String trangThai) {
        return ResponseEntity.ok(laiThuService.updateTrangThaiLaiThu(id, trangThai));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaiThu(@PathVariable Long id) {
        laiThuService.deleteLaiThu(id);
        return ResponseEntity.noContent().build();
    }
}
package com.audistore.audi.controller;

import com.audistore.audi.dto.LichBaoDuongDTO;
import com.audistore.audi.service.LichBaoDuongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bao-duong")
public class LichBaoDuongController {

    private final LichBaoDuongService lichBaoDuongService;

    @Autowired
    public LichBaoDuongController(LichBaoDuongService lichBaoDuongService) {
        this.lichBaoDuongService = lichBaoDuongService;
    }

    @GetMapping
    public ResponseEntity<List<LichBaoDuongDTO>> getAllLichBaoDuong() {
        return ResponseEntity.ok(lichBaoDuongService.getAllLichBaoDuong());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LichBaoDuongDTO> getLichBaoDuongById(@PathVariable Long id) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongById(id));
    }
    
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<LichBaoDuongDTO>> getLichBaoDuongByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongByNguoiDung(idNguoiDung));
    }
    
    @GetMapping("/dai-ly/{idDaiLy}")
    public ResponseEntity<List<LichBaoDuongDTO>> getLichBaoDuongByDaiLy(@PathVariable Long idDaiLy) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongByDaiLy(idDaiLy));
    }
    
    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<LichBaoDuongDTO>> getLichBaoDuongByTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongByTrangThai(trangThai));
    }
    
    @GetMapping("/loai-dich-vu/{loaiDichVu}")
    public ResponseEntity<List<LichBaoDuongDTO>> getLichBaoDuongByLoaiDichVu(@PathVariable String loaiDichVu) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongByLoaiDichVu(loaiDichVu));
    }
    
    @GetMapping("/so-khung/{soKhung}")
    public ResponseEntity<List<LichBaoDuongDTO>> getLichBaoDuongBySoKhung(@PathVariable String soKhung) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongBySoKhung(soKhung));
    }
    
    @GetMapping("/ngay")
    public ResponseEntity<List<LichBaoDuongDTO>> getLichBaoDuongByNgay(
            @RequestParam Long idDaiLy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngay) {
        return ResponseEntity.ok(lichBaoDuongService.getLichBaoDuongByNgay(idDaiLy, ngay));
    }
    
    @PostMapping
    public ResponseEntity<LichBaoDuongDTO> createLichBaoDuong(@Valid @RequestBody LichBaoDuongDTO lichBaoDuongDTO) {
        return new ResponseEntity<>(lichBaoDuongService.createLichBaoDuong(lichBaoDuongDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LichBaoDuongDTO> updateLichBaoDuong(@PathVariable Long id, @Valid @RequestBody LichBaoDuongDTO lichBaoDuongDTO) {
        return ResponseEntity.ok(lichBaoDuongService.updateLichBaoDuong(id, lichBaoDuongDTO));
    }
    
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<LichBaoDuongDTO> updateTrangThaiLichBaoDuong(
            @PathVariable Long id,
            @RequestParam String trangThai) {
        return ResponseEntity.ok(lichBaoDuongService.updateTrangThaiLichBaoDuong(id, trangThai));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLichBaoDuong(@PathVariable Long id) {
        lichBaoDuongService.deleteLichBaoDuong(id);
        return ResponseEntity.noContent().build();
    }
    
    // API chuyên biệt cho đăng ký bảo dưỡng định kỳ
    @PostMapping("/dinh-ky")
    public ResponseEntity<LichBaoDuongDTO> dangKyBaoDuongDinhKy(@Valid @RequestBody LichBaoDuongDTO lichBaoDuongDTO) {
        // Đặt loại dịch vụ là bảo dưỡng định kỳ
        lichBaoDuongDTO.setLoaiDichVu("bao_duong_dinh_ky");
        return new ResponseEntity<>(lichBaoDuongService.createLichBaoDuong(lichBaoDuongDTO), HttpStatus.CREATED);
    }
}
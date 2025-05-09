package com.audistore.audi.controller;

import com.audistore.audi.dto.ThanhToanDTO;
import com.audistore.audi.service.ThanhToanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/thanh-toan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    @Autowired
    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @GetMapping
    public ResponseEntity<List<ThanhToanDTO>> getAllThanhToan() {
        return ResponseEntity.ok(thanhToanService.getAllThanhToan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThanhToanDTO> getThanhToanById(@PathVariable Long id) {
        return ResponseEntity.ok(thanhToanService.getThanhToanById(id));
    }
    
    @GetMapping("/don-hang/{idDonHang}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByDonHang(@PathVariable Long idDonHang) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByDonHang(idDonHang));
    }
    
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByNguoiDung(idNguoiDung));
    }
    
    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByTrangThai(trangThai));
    }
    
    @GetMapping("/loai/{loaiThanhToan}")
    public ResponseEntity<List<ThanhToanDTO>> getThanhToanByLoai(@PathVariable String loaiThanhToan) {
        return ResponseEntity.ok(thanhToanService.getThanhToanByLoai(loaiThanhToan));
    }
    
    @PostMapping
    public ResponseEntity<ThanhToanDTO> createThanhToan(@Valid @RequestBody ThanhToanDTO thanhToanDTO) {
        return new ResponseEntity<>(thanhToanService.createThanhToan(thanhToanDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ThanhToanDTO> updateThanhToan(@PathVariable Long id, @Valid @RequestBody ThanhToanDTO thanhToanDTO) {
        return ResponseEntity.ok(thanhToanService.updateThanhToan(id, thanhToanDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThanhToan(@PathVariable Long id) {
        thanhToanService.deleteThanhToan(id);
        return ResponseEntity.noContent().build();
    }
}
package com.audistore.audi.controller;

import com.audistore.audi.dto.KhuyenMaiDTO;
import com.audistore.audi.service.KhuyenMaiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/khuyen-mai")
public class KhuyenMaiController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;
    
    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<KhuyenMaiDTO> taoKhuyenMai(@Valid @RequestBody KhuyenMaiDTO khuyenMaiDTO) {
        return new ResponseEntity<>(khuyenMaiService.taoKhuyenMai(khuyenMaiDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiDTO> getKhuyenMaiById(@PathVariable Long id) {
        return ResponseEntity.ok(khuyenMaiService.getKhuyenMaiById(id));
    }
    
    @GetMapping("/ma/{maKhuyenMai}")
    public ResponseEntity<KhuyenMaiDTO> getKhuyenMaiByMa(@PathVariable String maKhuyenMai) {
        return ResponseEntity.ok(khuyenMaiService.getKhuyenMaiByMa(maKhuyenMai));
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKhuyenMai(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayKetThuc") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<KhuyenMaiDTO> khuyenMaiPage = khuyenMaiService.getAllKhuyenMai(page, size, sortBy, sortDir);
        
        Map<String, Object> response = new HashMap<>();
        response.put("khuyenMai", khuyenMaiPage.getContent());
        response.put("trangHienTai", khuyenMaiPage.getNumber());
        response.put("tongItem", khuyenMaiPage.getTotalElements());
        response.put("tongTrang", khuyenMaiPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/con-hieu-luc")
    public ResponseEntity<Map<String, Object>> getKhuyenMaiConHieuLuc(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        
        Page<KhuyenMaiDTO> khuyenMaiPage = khuyenMaiService.getKhuyenMaiConHieuLuc(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("khuyenMai", khuyenMaiPage.getContent());
        response.put("trangHienTai", khuyenMaiPage.getNumber());
        response.put("tongItem", khuyenMaiPage.getTotalElements());
        response.put("tongTrang", khuyenMaiPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sap-het-han")
    public ResponseEntity<Map<String, Object>> getKhuyenMaiSapHetHan(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        
        Page<KhuyenMaiDTO> khuyenMaiPage = khuyenMaiService.getKhuyenMaiSapHetHan(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("khuyenMai", khuyenMaiPage.getContent());
        response.put("trangHienTai", khuyenMaiPage.getNumber());
        response.put("tongItem", khuyenMaiPage.getTotalElements());
        response.put("tongTrang", khuyenMaiPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<KhuyenMaiDTO> capNhatKhuyenMai(
            @PathVariable Long id, 
            @Valid @RequestBody KhuyenMaiDTO khuyenMaiDTO) {
        return ResponseEntity.ok(khuyenMaiService.capNhatKhuyenMai(id, khuyenMaiDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Map<String, Boolean>> xoaKhuyenMai(@PathVariable Long id) {
        khuyenMaiService.xoaKhuyenMai(id);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("daXoa", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/mau-xe/{idMauXe}")
    public ResponseEntity<List<KhuyenMaiDTO>> timKhuyenMaiChoMauXe(@PathVariable Long idMauXe) {
        return ResponseEntity.ok(khuyenMaiService.timKhuyenMaiChoMauXe(idMauXe));
    }
    
    @GetMapping("/tinh-gia")
    public ResponseEntity<Map<String, Object>> tinhGiaSauKhuyenMai(
            @RequestParam BigDecimal giaBanDau,
            @RequestParam Long idKhuyenMai) {
        
        BigDecimal giaSauKhuyenMai = khuyenMaiService.tinhGiaSauKhuyenMai(giaBanDau, idKhuyenMai);
        
        Map<String, Object> response = new HashMap<>();
        response.put("giaBanDau", giaBanDau);
        response.put("giaSauKhuyenMai", giaSauKhuyenMai);
        response.put("soTienGiam", giaBanDau.subtract(giaSauKhuyenMai));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/kiem-tra-ap-dung")
    public ResponseEntity<Map<String, Object>> kiemTraApDungKhuyenMai(
            @RequestParam Long idKhuyenMai,
            @RequestParam List<Long> idMauXes,
            @RequestParam BigDecimal tongGiaTri) {
        
        boolean hopLe = khuyenMaiService.kiemTraKhuyenMaiHopLe(idKhuyenMai, idMauXes, tongGiaTri);
        
        Map<String, Object> response = new HashMap<>();
        response.put("idKhuyenMai", idKhuyenMai);
        response.put("hopLe", hopLe);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/tang-su-dung")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    public ResponseEntity<Map<String, Boolean>> tangSoLanSuDung(@PathVariable Long id) {
        khuyenMaiService.tangSoLanSuDung(id);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("thanhCong", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
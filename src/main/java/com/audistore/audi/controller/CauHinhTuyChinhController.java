package com.audistore.audi.controller;

import com.audistore.audi.dto.CauHinhTuyChinhDTO;
import com.audistore.audi.security.service.UserDetailsImpl;
import com.audistore.audi.service.CauHinhTuyChinhService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cau-hinh")
public class CauHinhTuyChinhController {

    private final CauHinhTuyChinhService cauHinhTuyChinhService;

    @Autowired
    public CauHinhTuyChinhController(CauHinhTuyChinhService cauHinhTuyChinhService) {
        this.cauHinhTuyChinhService = cauHinhTuyChinhService;
    }

    @GetMapping
    public ResponseEntity<List<CauHinhTuyChinhDTO>> getAllCauHinhTuyChinh() {
        return ResponseEntity.ok(cauHinhTuyChinhService.getAllCauHinhTuyChinh());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CauHinhTuyChinhDTO> getCauHinhTuyChinhById(@PathVariable Long id) {
        return ResponseEntity.ok(cauHinhTuyChinhService.getCauHinhTuyChinhById(id));
    }
    
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<CauHinhTuyChinhDTO>> getCauHinhTuyChinhByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(cauHinhTuyChinhService.getCauHinhTuyChinhByNguoiDung(idNguoiDung));
    }
    
    @GetMapping("/mau-xe/{idMauXe}")
    public ResponseEntity<List<CauHinhTuyChinhDTO>> getCauHinhTuyChinhByMauXe(@PathVariable Long idMauXe) {
        return ResponseEntity.ok(cauHinhTuyChinhService.getCauHinhTuyChinhByMauXe(idMauXe));
    }
    
    @GetMapping("/tinh-gia")
    public ResponseEntity<BigDecimal> tinhGiaCauHinh(
            @RequestParam Long idMauXe,
            @RequestParam Long idMauSac,
            @RequestParam(required = false) List<Long> idTuyChon) {
        return ResponseEntity.ok(cauHinhTuyChinhService.tinhTongGia(idMauXe, idMauSac, idTuyChon));
    }
    
    @PostMapping
    public ResponseEntity<CauHinhTuyChinhDTO> createCauHinhTuyChinh(
            @Valid @RequestBody CauHinhTuyChinhDTO cauHinhTuyChinhDTO) {
        return new ResponseEntity<>(cauHinhTuyChinhService.createCauHinhTuyChinh(cauHinhTuyChinhDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CauHinhTuyChinhDTO> updateCauHinhTuyChinh(
            @PathVariable Long id, 
            @Valid @RequestBody CauHinhTuyChinhDTO cauHinhTuyChinhDTO) {
        return ResponseEntity.ok(cauHinhTuyChinhService.updateCauHinhTuyChinh(id, cauHinhTuyChinhDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCauHinhTuyChinh(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cauHinhTuyChinhService.deleteCauHinhTuyChinh(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
    
    // API để tùy chỉnh nhanh một mẫu xe
    @PostMapping("/tuy-chinh-nhanh")
    public ResponseEntity<CauHinhTuyChinhDTO> tuyChinhNhanh(
            @RequestParam Long idNguoiDung,
            @RequestParam Long idMauXe,
            @RequestParam Long idMauSac,
            @RequestParam(required = false) List<Long> idTuyChon,
            @RequestParam(required = false) String ten) {
        
        CauHinhTuyChinhDTO dto = new CauHinhTuyChinhDTO();
        dto.setIdNguoiDung(idNguoiDung);
        dto.setIdMau(idMauXe);
        dto.setIdMauSac(idMauSac);
        dto.setDanhSachIdTuyChon(idTuyChon);
        dto.setTen(ten);
        
        // Tính tổng giá tự động
        BigDecimal tongGia = cauHinhTuyChinhService.tinhTongGia(idMauXe, idMauSac, idTuyChon);
        dto.setTongGia(tongGia);
        
        return new ResponseEntity<>(cauHinhTuyChinhService.createCauHinhTuyChinh(dto), HttpStatus.CREATED);
    }
}
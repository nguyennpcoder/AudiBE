package com.audistore.audi.controller;

import com.audistore.audi.dto.DonHangDTO;
import com.audistore.audi.dto.KhuyenMaiDTO;
import com.audistore.audi.service.DonHangService;
import com.audistore.audi.service.KhuyenMaiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/don-hang")
public class DonHangController {

    private final DonHangService donHangService;
    private final KhuyenMaiService khuyenMaiService;

    @Autowired
    public DonHangController(DonHangService donHangService, KhuyenMaiService khuyenMaiService) {
        this.donHangService = donHangService;
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping
    public ResponseEntity<List<DonHangDTO>> getAllDonHang() {
        return ResponseEntity.ok(donHangService.getAllDonHang());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonHangDTO> getDonHangById(@PathVariable Long id) {
        return ResponseEntity.ok(donHangService.getDonHangById(id));
    }
    
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<DonHangDTO>> getDonHangByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(donHangService.getDonHangByNguoiDung(idNguoiDung));
    }
    
    @GetMapping("/dai-ly/{idDaiLy}")
    public ResponseEntity<List<DonHangDTO>> getDonHangByDaiLy(@PathVariable Long idDaiLy) {
        return ResponseEntity.ok(donHangService.getDonHangByDaiLy(idDaiLy));
    }
    
    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<DonHangDTO>> getDonHangByTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(donHangService.getDonHangByTrangThai(trangThai));
    }
    
    @PostMapping
    public ResponseEntity<DonHangDTO> createDonHang(@Valid @RequestBody DonHangDTO donHangDTO) {
        return new ResponseEntity<>(donHangService.createDonHang(donHangDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DonHangDTO> updateDonHang(@PathVariable Long id, @Valid @RequestBody DonHangDTO donHangDTO) {
        return ResponseEntity.ok(donHangService.updateDonHang(id, donHangDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonHang(@PathVariable Long id) {
        donHangService.deleteDonHang(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/kiem-tra-khuyen-mai")
    public ResponseEntity<Map<String, Object>> kiemTraKhuyenMai(
            @RequestParam Long idKhuyenMai,
            @RequestParam List<Long> idMauXes,
            @RequestParam BigDecimal tongGiaTri) {
        
        boolean hopLe = khuyenMaiService.kiemTraKhuyenMaiHopLe(idKhuyenMai, idMauXes, tongGiaTri);
        
        if (hopLe) {
            BigDecimal giaSauKhuyenMai = khuyenMaiService.tinhGiaSauKhuyenMai(tongGiaTri, idKhuyenMai);
            BigDecimal tienGiam = tongGiaTri.subtract(giaSauKhuyenMai);
            
            KhuyenMaiDTO khuyenMai = khuyenMaiService.getKhuyenMaiById(idKhuyenMai);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hopLe", true);
            response.put("khuyenMai", khuyenMai);
            response.put("giaBanDau", tongGiaTri);
            response.put("giaSauKhuyenMai", giaSauKhuyenMai);
            response.put("tienGiam", tienGiam);
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("hopLe", false);
            response.put("thongBao", "Khuyến mãi không áp dụng được cho đơn hàng này");
            
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/khuyen-mai-phu-hop")
    public ResponseEntity<List<KhuyenMaiDTO>> timKhuyenMaiPhuHop(
            @RequestParam List<Long> idMauXes) {
        
        if (!idMauXes.isEmpty()) {
            return ResponseEntity.ok(khuyenMaiService.timKhuyenMaiChoMauXe(idMauXes.get(0)));
        }
        
        return ResponseEntity.ok(List.of());
    }
}
package com.audistore.audi.controller;

import com.audistore.audi.dto.CauHinhTuyChinhDTO;
import com.audistore.audi.security.service.UserDetailsImpl;
import com.audistore.audi.service.CauHinhTuyChinhService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Nhận request lấy cấu hình cho người dùng ID: {}", idNguoiDung);
        
        try {
            List<CauHinhTuyChinhDTO> result = cauHinhTuyChinhService.getCauHinhTuyChinhByNguoiDung(idNguoiDung);
            log.info("Trả về {} cấu hình cho người dùng ID: {}", result.size(), idNguoiDung);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy cấu hình cho người dùng ID {}: {}", idNguoiDung, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
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
        try {
            log.info("Nhận request tạo cấu hình: {}", cauHinhTuyChinhDTO);
            
            CauHinhTuyChinhDTO createdCauHinh = cauHinhTuyChinhService.createCauHinhTuyChinh(cauHinhTuyChinhDTO);
            
            log.info("Đã tạo cấu hình thành công với ID: {}", createdCauHinh.getId());
            
            return new ResponseEntity<>(createdCauHinh, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Lỗi khi tạo cấu hình: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
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



    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> xuatPDF(@PathVariable Long id) {
        try {
            log.info("Đang xuất PDF cho cấu hình ID: {}", id);
            
            // Lấy cấu hình theo ID
            CauHinhTuyChinhDTO cauHinh = cauHinhTuyChinhService.getCauHinhTuyChinhById(id);
            if (cauHinh == null) {
                log.warn("Không tìm thấy cấu hình với ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            // Tạo PDF
            byte[] pdfBytes = cauHinhTuyChinhService.xuatPDF(cauHinh);
            
            // Tạo resource từ byte array
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            // Tạo filename an toàn
            String safeName = cauHinh.getTenMau() != null ? 
                cauHinh.getTenMau().replaceAll("[^a-zA-Z0-9\\s-]", "") : "unknown";
            String filename = String.format("cau-hinh-xe-%s-%s.pdf", 
                safeName,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
            
            log.info("Đã tạo PDF thành công: {}", filename);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition, Content-Length, Content-Type")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
                
        } catch (Exception e) {
            log.error("Lỗi khi xuất PDF: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ByteArrayResource("Lỗi khi tạo PDF".getBytes()));
        }
    }
}
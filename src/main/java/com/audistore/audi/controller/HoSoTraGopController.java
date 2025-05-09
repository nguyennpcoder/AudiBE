package com.audistore.audi.controller;

import com.audistore.audi.annotation.LogActivity;
import com.audistore.audi.dto.HoSoTraGopDTO;
import com.audistore.audi.security.service.UserDetailsImpl;
import com.audistore.audi.service.HoSoTraGopService;
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
@RequestMapping("/api/v1/tra-gop")
public class HoSoTraGopController {

    private final HoSoTraGopService hoSoTraGopService;

    @Autowired
    public HoSoTraGopController(HoSoTraGopService hoSoTraGopService) {
        this.hoSoTraGopService = hoSoTraGopService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    public ResponseEntity<Page<HoSoTraGopDTO>> getAllHoSoTraGop(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayNopHoSo") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        return ResponseEntity.ok(hoSoTraGopService.getAllHoSoTraGop(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HoSoTraGopDTO> getHoSoTraGopById(@PathVariable Long id) {
        return ResponseEntity.ok(hoSoTraGopService.getHoSoTraGopById(id));
    }

    @GetMapping("/don-hang/{idDonHang}")
    public ResponseEntity<List<HoSoTraGopDTO>> getHoSoTraGopByDonHang(@PathVariable Long idDonHang) {
        return ResponseEntity.ok(hoSoTraGopService.getHoSoTraGopByDonHang(idDonHang));
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<List<HoSoTraGopDTO>> getHoSoTraGopByNguoiDung(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(hoSoTraGopService.getHoSoTraGopByNguoiDung(idNguoiDung));
    }

    @GetMapping("/trang-thai/{trangThai}")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    public ResponseEntity<Page<HoSoTraGopDTO>> getHoSoTraGopByTrangThai(
            @PathVariable String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(hoSoTraGopService.getHoSoTraGopByTrangThai(trangThai, page, size));
    }

    @PostMapping
    @LogActivity(type = "tao_ho_so_tra_gop")
    public ResponseEntity<HoSoTraGopDTO> createHoSoTraGop(
            @Valid @RequestBody HoSoTraGopDTO hoSoTraGopDTO,
            Authentication authentication) {
        
        // Lấy thông tin người dùng hiện tại
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();
        
        HoSoTraGopDTO createdHoSo = hoSoTraGopService.createHoSoTraGop(hoSoTraGopDTO, currentUserId);
        return new ResponseEntity<>(createdHoSo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @LogActivity(type = "cap_nhat_ho_so_tra_gop")
    public ResponseEntity<HoSoTraGopDTO> updateHoSoTraGop(
            @PathVariable Long id, 
            @Valid @RequestBody HoSoTraGopDTO hoSoTraGopDTO) {
        return ResponseEntity.ok(hoSoTraGopService.updateHoSoTraGop(id, hoSoTraGopDTO));
    }

    @PatchMapping("/{id}/phe-duyet")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    @LogActivity(type = "phe_duyet_ho_so_tra_gop")
    public ResponseEntity<HoSoTraGopDTO> pheDuyetHoSo(@PathVariable Long id) {
        return ResponseEntity.ok(hoSoTraGopService.pheDuyetHoSo(id));
    }

        @PatchMapping("/{id}/tu-choi")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    @LogActivity(type = "tu_choi_ho_so_tra_gop")
    public ResponseEntity<HoSoTraGopDTO> tuChoiHoSo(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        String lyDo = payload.get("lyDo");
        return ResponseEntity.ok(hoSoTraGopService.tuChoiHoSo(id, lyDo));
    }
    
    @PatchMapping("/{id}/hoan-thanh")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    @LogActivity(type = "hoan_thanh_ho_so_tra_gop")
    public ResponseEntity<HoSoTraGopDTO> hoanThanhHoSo(@PathVariable Long id) {
        return ResponseEntity.ok(hoSoTraGopService.hoanThanhHoSo(id));
    }
    
    @GetMapping("/thong-ke")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    public ResponseEntity<Map<String, Long>> getThongKeHoSoTraGop() {
        return ResponseEntity.ok(hoSoTraGopService.getThongKeHoSoTraGop());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    @LogActivity(type = "xoa_ho_so_tra_gop")
    public ResponseEntity<Map<String, String>> deleteHoSoTraGop(@PathVariable Long id) {
        hoSoTraGopService.deleteHoSoTraGop(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa hồ sơ trả góp thành công");
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
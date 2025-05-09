package com.audistore.audi.controller;

import com.audistore.audi.dto.NguoiDungDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nguoi-dung")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;
    private final NguoiDungRepository nguoiDungRepository;

    @Autowired
    public NguoiDungController(NguoiDungService nguoiDungService, NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungService = nguoiDungService;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @GetMapping
    public ResponseEntity<List<NguoiDungDTO>> getAllNguoiDung() {
        return ResponseEntity.ok(nguoiDungService.getAllNguoiDung());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> getNguoiDungById(@PathVariable Long id) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        NguoiDung currentUser = nguoiDungRepository.findByEmail(currentUserEmail).orElse(null);
        
        // Kiểm tra xem người dùng hiện tại có phải là admin hoặc đang xem chính thông tin của mình
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        
        if (currentUser != null && (isAdmin || currentUser.getId().equals(id))) {
            return ResponseEntity.ok(nguoiDungService.getNguoiDungById(id));
        } else {
            throw new AccessDeniedException("Bạn không có quyền xem thông tin của người dùng khác");
        }
    }

    @PostMapping
    public ResponseEntity<NguoiDungDTO> createNguoiDung(@RequestBody NguoiDungDTO nguoiDungDTO) {
        return ResponseEntity.ok(nguoiDungService.createNguoiDung(nguoiDungDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> updateNguoiDung(@PathVariable Long id, @RequestBody NguoiDungDTO nguoiDungDTO) {
        // Kiểm tra quyền tương tự như trong getNguoiDungById
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        NguoiDung currentUser = nguoiDungRepository.findByEmail(currentUserEmail).orElse(null);
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        
        if (currentUser != null && (isAdmin || currentUser.getId().equals(id))) {
            return ResponseEntity.ok(nguoiDungService.updateNguoiDung(id, nguoiDungDTO));
        } else {
            throw new AccessDeniedException("Bạn không có quyền cập nhật thông tin của người dùng khác");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNguoiDung(@PathVariable Long id) {
        // Chỉ admin mới có quyền xóa người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        
        if (isAdmin) {
            nguoiDungService.deleteNguoiDung(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccessDeniedException("Bạn không có quyền xóa người dùng");
        }
    }

    @GetMapping("/profile")
public ResponseEntity<NguoiDungDTO> getCurrentUserProfile() {
    // Lấy thông tin người dùng
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    
    NguoiDung nguoiDung = nguoiDungRepository.findByEmail(currentUserEmail)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng hiện tại"));
    
    // Get user data
    NguoiDungDTO userDTO = nguoiDungService.getNguoiDungById(nguoiDung.getId());
    
    // Set a masked password for display
    userDTO.setMatKhau("********");
    
    return ResponseEntity.ok(userDTO);
}
}
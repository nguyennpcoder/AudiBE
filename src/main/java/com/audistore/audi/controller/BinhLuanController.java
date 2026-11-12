package com.audistore.audi.controller;

import com.audistore.audi.dto.BinhLuanDTO;
import com.audistore.audi.security.service.UserDetailsImpl;
import com.audistore.audi.service.BinhLuanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import com.audistore.audi.model.BinhLuan;
import com.audistore.audi.repository.BinhLuanRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.security.Key;

@RestController
@RequestMapping("/api/v1/binh-luan")
public class BinhLuanController {

    @Autowired
    private BinhLuanService binhLuanService;

    @Autowired
    private BinhLuanRepository binhLuanRepository;

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long jwtExpirationMs = 86400000; // 24 hours

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(String.valueOf(userPrincipal.getId())) // Sử dụng ID thay vì email
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    @PostMapping
    public ResponseEntity<BinhLuanDTO> themBinhLuan(@Valid @RequestBody BinhLuanDTO dto) {
        BinhLuanDTO createdBinhLuan = binhLuanService.themBinhLuan(dto);
        return new ResponseEntity<>(createdBinhLuan, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeBinhLuan(
            @PathVariable Long id,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Bạn cần đăng nhập để thích bình luận"));
        }
        
        try {
            // Lấy user ID từ UserDetails thay vì parse từ authentication.getName()
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            
            Map<String, Object> result = binhLuanService.likeBinhLuan(id, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Map<String, Object>> unlikeBinhLuan(
            @PathVariable Long id,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Bạn cần đăng nhập để bỏ thích bình luận"));
        }
        
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            
            Map<String, Object> result = binhLuanService.unlikeBinhLuan(id, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bai-viet/{idBaiViet}")
    public ResponseEntity<Map<String, Object>> getBinhLuanByBaiViet(
            @PathVariable Long idBaiViet,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            currentUserId = userDetails.getId();
        }
        
        // Nếu size >= 100, lấy tất cả comments không phân trang
        if (size >= 100) {
            List<BinhLuanDTO> allComments = binhLuanService.getAllBinhLuanByBaiViet(idBaiViet, currentUserId);
            Long tongSoBinhLuan = binhLuanService.demBinhLuanByBaiViet(idBaiViet);
            
            Map<String, Object> response = new HashMap<>();
            response.put("binhLuan", allComments);
            response.put("currentPage", 0);
            response.put("totalItems", tongSoBinhLuan);
            response.put("totalPages", 1);
            response.put("tongSoBinhLuan", tongSoBinhLuan);
            
            return ResponseEntity.ok(response);
        }
        
        // Giữ nguyên logic phân trang cũ
        Page<BinhLuanDTO> binhLuanPage = binhLuanService.getBinhLuanByBaiViet(idBaiViet, page, size);
        Long tongSoBinhLuan = binhLuanService.demBinhLuanByBaiViet(idBaiViet);
        
        Map<String, Object> response = new HashMap<>();
        response.put("binhLuan", binhLuanPage.getContent());
        response.put("currentPage", binhLuanPage.getNumber());
        response.put("totalItems", binhLuanPage.getTotalElements());
        response.put("totalPages", binhLuanPage.getTotalPages());
        response.put("tongSoBinhLuan", tongSoBinhLuan);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BinhLuanDTO> getBinhLuanById(@PathVariable Long id) {
        BinhLuanDTO binhLuan = binhLuanService.getBinhLuanById(id);
        return ResponseEntity.ok(binhLuan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BinhLuanDTO> capNhatBinhLuan(
            @PathVariable Long id,
            @Valid @RequestBody BinhLuanDTO dto,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idNguoiCapNhat = userDetails.getId();
        BinhLuanDTO updatedBinhLuan = binhLuanService.capNhatBinhLuan(id, dto, idNguoiCapNhat);
        return ResponseEntity.ok(updatedBinhLuan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaBinhLuan(
            @PathVariable Long id,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idNguoiXoa = userDetails.getId();
        binhLuanService.xoaBinhLuan(id, idNguoiXoa);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('ROLE_QUAN_TRI')")
    public ResponseEntity<Void> thayDoiTrangThai(
            @PathVariable Long id,
            @RequestParam String trangThai,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idNguoiThayDoi = userDetails.getId();
        binhLuanService.thayDoiTrangThai(id, trangThai, idNguoiThayDoi);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bai-viet/{idBaiViet}/count")
    public ResponseEntity<Map<String, Long>> demBinhLuanByBaiViet(@PathVariable Long idBaiViet) {
        Long count = binhLuanService.demBinhLuanByBaiViet(idBaiViet);
        Map<String, Long> response = new HashMap<>();
        response.put("tongSoBinhLuan", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bai-viet/{idBaiViet}/debug")
    public ResponseEntity<Map<String, Object>> debugBinhLuanByBaiViet(@PathVariable Long idBaiViet) {
        // Lấy tất cả comments không phân trang để debug
        List<BinhLuan> allComments = binhLuanRepository.findAllByBaiVietId(idBaiViet);
        
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("totalCommentsInDB", allComments.size());
        debugInfo.put("commentsByStatus", allComments.stream()
            .collect(Collectors.groupingBy(b -> b.getTrangThai().name())));
        debugInfo.put("commentsWithParent", allComments.stream()
            .filter(b -> b.getBinhLuanCha() != null)
            .map(b -> Map.of(
                "id", b.getId(),
                "parentId", b.getBinhLuanCha().getId(),
                "status", b.getTrangThai().name()
            ))
            .collect(Collectors.toList()));
        
        return ResponseEntity.ok(debugInfo);
    }
} 
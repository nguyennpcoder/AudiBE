package com.audistore.audi.controller;

import com.audistore.audi.dto.BaiVietDTO;
import com.audistore.audi.service.BaiVietService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bai-viet")
public class BaiVietController {

    private final BaiVietService baiVietService;

    @Autowired
    public BaiVietController(BaiVietService baiVietService) {
        this.baiVietService = baiVietService;
    }

    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI') or hasRole('HO_TRO')")
    public ResponseEntity<List<BaiVietDTO>> getAllBaiViet() {
        return ResponseEntity.ok(baiVietService.getAllBaiViet());
    }
    
    @GetMapping("/page")
    @PreAuthorize("hasRole('QUAN_TRI') or hasRole('HO_TRO')")
    public ResponseEntity<Map<String, Object>> getPagedBaiViet(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayDang") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<BaiVietDTO> baiVietPage = baiVietService.getPagedBaiViet(page, size, sortBy, sortDir);
        
        Map<String, Object> response = new HashMap<>();
        response.put("baiViet", baiVietPage.getContent());
        response.put("currentPage", baiVietPage.getNumber());
        response.put("totalItems", baiVietPage.getTotalElements());
        response.put("totalPages", baiVietPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicBaiViet(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<BaiVietDTO> baiVietPage = baiVietService.getPublicBaiViet(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("baiViet", baiVietPage.getContent());
        response.put("currentPage", baiVietPage.getNumber());
        response.put("totalItems", baiVietPage.getTotalElements());
        response.put("totalPages", baiVietPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaiVietDTO> getBaiVietById(@PathVariable Long id) {
        return ResponseEntity.ok(baiVietService.getBaiVietById(id));
    }
    
    @GetMapping("/tac-gia/{idTacGia}")
    public ResponseEntity<List<BaiVietDTO>> getBaiVietByTacGia(@PathVariable Long idTacGia) {
        return ResponseEntity.ok(baiVietService.getBaiVietByTacGia(idTacGia));
    }
    
    @GetMapping("/danh-muc/{danhMuc}")
    public ResponseEntity<List<BaiVietDTO>> getBaiVietByDanhMuc(@PathVariable String danhMuc) {
        return ResponseEntity.ok(baiVietService.getBaiVietByDanhMuc(danhMuc));
    }
    
    @GetMapping("/danh-muc/{danhMuc}/public")
    public ResponseEntity<Map<String, Object>> getPublicBaiVietByDanhMuc(
            @PathVariable String danhMuc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<BaiVietDTO> baiVietPage = baiVietService.getPublicBaiVietByDanhMuc(danhMuc, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("baiViet", baiVietPage.getContent());
        response.put("currentPage", baiVietPage.getNumber());
        response.put("totalItems", baiVietPage.getTotalElements());
        response.put("totalPages", baiVietPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('QUAN_TRI') or hasRole('HO_TRO')")
    public ResponseEntity<List<BaiVietDTO>> searchBaiViet(@RequestParam String keyword) {
        return ResponseEntity.ok(baiVietService.searchBaiViet(keyword));
    }
    
    @GetMapping("/search/public")
    public ResponseEntity<Map<String, Object>> searchPublicBaiViet(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<BaiVietDTO> baiVietPage = baiVietService.searchPublicBaiViet(keyword, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("baiViet", baiVietPage.getContent());
        response.put("currentPage", baiVietPage.getNumber());
        response.put("totalItems", baiVietPage.getTotalElements());
        response.put("totalPages", baiVietPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<BaiVietDTO>> getBaiVietByTag(@PathVariable String tag) {
        return ResponseEntity.ok(baiVietService.getBaiVietByTag(tag));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI') or hasRole('HO_TRO')")
    public ResponseEntity<BaiVietDTO> createBaiViet(@Valid @RequestBody BaiVietDTO baiVietDTO) {
        return new ResponseEntity<>(baiVietService.createBaiViet(baiVietDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI') or hasRole('HO_TRO')")
    public ResponseEntity<BaiVietDTO> updateBaiViet(
            @PathVariable Long id, 
            @Valid @RequestBody BaiVietDTO baiVietDTO) {
        return ResponseEntity.ok(baiVietService.updateBaiViet(id, baiVietDTO));
    }
    
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('QUAN_TRI') or hasRole('HO_TRO')")
    public ResponseEntity<BaiVietDTO> updateTrangThaiBaiViet(
            @PathVariable Long id,
            @RequestParam Boolean daXuatBan) {
        return ResponseEntity.ok(baiVietService.updateTrangThaiBaiViet(id, daXuatBan));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteBaiViet(@PathVariable Long id) {
        baiVietService.deleteBaiViet(id);
        return ResponseEntity.noContent().build();
    }
}
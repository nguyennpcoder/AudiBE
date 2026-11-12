package com.audistore.audi.controller;

import com.audistore.audi.dto.BaiVietDTO;
import com.audistore.audi.service.BaiVietService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
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
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
    public ResponseEntity<List<BaiVietDTO>> getAllBaiViet() {
        return ResponseEntity.ok(baiVietService.getAllBaiViet());
    }
    
    @GetMapping("/page")
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
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
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
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
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
    public ResponseEntity<BaiVietDTO> createBaiViet(@Valid @RequestBody BaiVietDTO baiVietDTO) {
        return new ResponseEntity<>(baiVietService.createBaiViet(baiVietDTO), HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
    public ResponseEntity<BaiVietDTO> createBaiVietWithFormData(
            @RequestParam("tieuDe") String tieuDe,
            @RequestParam("noiDung") String noiDung,
            @RequestParam(value = "anhDaiDien", required = false) MultipartFile anhDaiDien,
            @RequestParam("idTacGia") Long idTacGia,
            @RequestParam("danhMuc") String danhMuc,
            @RequestParam(value = "theGan", required = false) String theGan,
            @RequestParam(value = "daXuatBan", defaultValue = "false") Boolean daXuatBan) { // Thay đổi default thành false
        
        System.out.println("Received create request with:");
        System.out.println("tieuDe: " + tieuDe);
        System.out.println("noiDung: " + noiDung);
        System.out.println("idTacGia: " + idTacGia);
        System.out.println("danhMuc: " + danhMuc);
        System.out.println("theGan: " + theGan);
        System.out.println("daXuatBan: " + daXuatBan);
        System.out.println("anhDaiDien: " + (anhDaiDien != null ? anhDaiDien.getOriginalFilename() : "null"));
        
        BaiVietDTO baiVietDTO = new BaiVietDTO();
        baiVietDTO.setTieuDe(tieuDe);
        baiVietDTO.setNoiDung(noiDung);
        baiVietDTO.setIdTacGia(idTacGia);
        baiVietDTO.setDanhMuc(danhMuc);
        baiVietDTO.setDaXuatBan(daXuatBan); // Mặc định là false (bản nháp)
        
        // Xử lý file upload
        if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
            try {
                System.out.println("Processing file upload...");
                // Tạo tên file với timestamp
                String fileName = System.currentTimeMillis() + "_" + anhDaiDien.getOriginalFilename();
                System.out.println("Generated filename: " + fileName);
                
                // Đường dẫn lưu file
                Path uploadPath = Paths.get("uploads/images/blogs/" + fileName);
                System.out.println("Upload path: " + uploadPath.toAbsolutePath());
                
                // Tạo thư mục nếu chưa tồn tại
                Files.createDirectories(uploadPath.getParent());
                System.out.println("Directory created/verified");
                
                // Lưu file
                Files.copy(anhDaiDien.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File saved successfully");
                
                // Set tên file vào DTO
                baiVietDTO.setAnhDaiDien(fileName);
                
            } catch (Exception e) {
                System.err.println("Error saving file: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
            }
        } else {
            System.out.println("No file uploaded");
        }
        
        // Xử lý tags
        if (theGan != null && !theGan.isEmpty()) {
            try {
                List<String> tags = Arrays.asList(theGan.split(","));
                baiVietDTO.setTheGan(tags);
                System.out.println("Tags processed: " + tags);
            } catch (Exception e) {
                baiVietDTO.setTheGan(Arrays.asList());
                System.err.println("Error processing tags: " + e.getMessage());
            }
        }
        
        System.out.println("Calling service to create BaiViet...");
        BaiVietDTO result = baiVietService.createBaiViet(baiVietDTO);
        System.out.println("BaiViet created successfully with ID: " + result.getId());
        
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @PutMapping(value = "/{id}/upload")
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
    public ResponseEntity<BaiVietDTO> updateBaiVietWithFormData(
            @PathVariable Long id,
            @RequestParam("tieuDe") String tieuDe,
            @RequestParam("noiDung") String noiDung,
            @RequestParam(value = "anhDaiDien", required = false) MultipartFile anhDaiDien,
            @RequestParam("idTacGia") Long idTacGia,
            @RequestParam("danhMuc") String danhMuc,
            @RequestParam(value = "theGan", required = false) String theGan,
            @RequestParam(value = "daXuatBan", defaultValue = "false") Boolean daXuatBan) {
        
        BaiVietDTO baiVietDTO = new BaiVietDTO();
        baiVietDTO.setId(id);
        baiVietDTO.setTieuDe(tieuDe);
        baiVietDTO.setNoiDung(noiDung);
        baiVietDTO.setIdTacGia(idTacGia);
        baiVietDTO.setDanhMuc(danhMuc);
        baiVietDTO.setDaXuatBan(daXuatBan);
        
        // Xử lý file upload
        if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
            try {
                // Tạo tên file với timestamp
                String fileName = System.currentTimeMillis() + "_" + anhDaiDien.getOriginalFilename();
                
                // Đường dẫn lưu file
                Path uploadPath = Paths.get("uploads/images/blogs/" + fileName);
                
                // Tạo thư mục nếu chưa tồn tại
                Files.createDirectories(uploadPath.getParent());
                
                // Lưu file
                Files.copy(anhDaiDien.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Set tên file vào DTO
                baiVietDTO.setAnhDaiDien(fileName);
                
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
            }
        }
        
        // Xử lý tags
        if (theGan != null && !theGan.isEmpty()) {
            try {
                List<String> tags = Arrays.asList(theGan.split(","));
                baiVietDTO.setTheGan(tags);
            } catch (Exception e) {
                baiVietDTO.setTheGan(Arrays.asList());
            }
        }
        
        return ResponseEntity.ok(baiVietService.updateBaiViet(id, baiVietDTO));
    }
    
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ROLE_QUAN_TRI') or hasRole('ROLE_HO_TRO')")
    public ResponseEntity<BaiVietDTO> updateTrangThaiBaiViet(
            @PathVariable Long id,
            @RequestParam Boolean daXuatBan) {
        return ResponseEntity.ok(baiVietService.updateTrangThaiBaiViet(id, daXuatBan));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_QUAN_TRI')")
    public ResponseEntity<Void> deleteBaiViet(@PathVariable Long id) {
        baiVietService.deleteBaiViet(id);
        return ResponseEntity.noContent().build();
    }
}
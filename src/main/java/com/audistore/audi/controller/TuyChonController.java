package com.audistore.audi.controller;

import com.audistore.audi.dto.TuyChonDTO;
import com.audistore.audi.service.TuyChonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tuy-chon")
@CrossOrigin(origins = "*")
public class TuyChonController {

    @Autowired
    private TuyChonService tuyChonService;

    @GetMapping
    public ResponseEntity<List<TuyChonDTO>> getAllTuyChon() {
        try {
            log.info("Đang lấy tất cả tùy chọn");
            List<TuyChonDTO> result = tuyChonService.getAllTuyChon();
            log.info("Đã lấy thành công {} tùy chọn", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả tùy chọn: ", e);
            return ResponseEntity.status(500)
                .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TuyChonDTO> getTuyChonById(@PathVariable Long id) {
        try {
            log.info("Đang lấy tùy chọn với ID: {}", id);
            TuyChonDTO result = tuyChonService.getTuyChonById(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy tùy chọn với ID {}: ", id, e);
            return ResponseEntity.status(500)
                .body(null);
        }
    }

    @GetMapping("/danh-muc/{danhMuc}")
    public ResponseEntity<List<TuyChonDTO>> getTuyChonByDanhMuc(@PathVariable String danhMuc) {
        try {
            log.info("Đang lấy tùy chọn theo danh mục: {}", danhMuc);
            List<TuyChonDTO> result = tuyChonService.getTuyChonByDanhMuc(danhMuc);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy tùy chọn theo danh mục {}: ", danhMuc, e);
            return ResponseEntity.status(500)
                .body(null);
        }
    }

    @GetMapping("/mau-xe/{mauXeId}")
    public ResponseEntity<List<TuyChonDTO>> getTuyChonByMauXe(@PathVariable Long mauXeId) {
        try {
            log.info("Đang lấy tùy chọn theo mẫu xe ID: {}", mauXeId);
            List<TuyChonDTO> result = tuyChonService.getTuyChonByMauXe(mauXeId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy tùy chọn theo mẫu xe ID {}: ", mauXeId, e);
            return ResponseEntity.status(500)
                .body(null);
        }
    }
} 
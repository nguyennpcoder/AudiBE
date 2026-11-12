package com.audistore.audi.controller;

import com.audistore.audi.service.NhatKyHoatDongService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nhat-ky")
public class NhatKyHoatDongIngestController {

    private final NhatKyHoatDongService nhatKyHoatDongService;

    @Autowired
    public NhatKyHoatDongIngestController(NhatKyHoatDongService nhatKyHoatDongService) {
        this.nhatKyHoatDongService = nhatKyHoatDongService;
    }

    // Endpoint cho client ghi log: chấp nhận mọi người dùng đã đăng nhập
    @PostMapping("/ingest")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> ingestActivity(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        String loaiHoatDong = (String) payload.getOrDefault("loaiHoatDong", "client_log");
        Object chiTiet = payload.get("chiTiet");

        Map<String, Object> chiTietMap = new HashMap<>();
        if (chiTiet instanceof Map) {
            //noinspection unchecked
            chiTietMap.putAll((Map<String, Object>) chiTiet);
        } else if (chiTiet != null) {
            chiTietMap.put("raw", chiTiet);
        }

        nhatKyHoatDongService.logHoatDong(loaiHoatDong, chiTietMap, request);

        return ResponseEntity.ok(Map.of("message", "Đã ghi nhận hoạt động"));
    }
}



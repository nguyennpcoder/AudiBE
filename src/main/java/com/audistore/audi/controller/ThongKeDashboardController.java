package com.audistore.audi.controller;

import com.audistore.audi.dto.ThongKeDashboardDTO;
import com.audistore.audi.service.ThongKeDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/thong-ke")
public class ThongKeDashboardController {
    
    @Autowired
    private ThongKeDashboardService thongKeDashboardService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('QUAN_TRI', 'BAN_HANG')")
    public ResponseEntity<ThongKeDashboardDTO> getThongKeDashboard() {
        return ResponseEntity.ok(thongKeDashboardService.getThongKeDashboard());
    }
}
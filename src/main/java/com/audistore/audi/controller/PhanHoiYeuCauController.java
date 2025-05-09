package com.audistore.audi.controller;

import com.audistore.audi.dto.PhanHoiYeuCauDTO;
import com.audistore.audi.service.PhanHoiYeuCauService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phan-hoi")
public class PhanHoiYeuCauController {

    @Autowired
    private PhanHoiYeuCauService phanHoiYeuCauService;

    @PostMapping
    public ResponseEntity<PhanHoiYeuCauDTO> taoPhanHoi(@Valid @RequestBody PhanHoiYeuCauDTO dto) {
        PhanHoiYeuCauDTO createdPhanHoi = phanHoiYeuCauService.taoPhanHoi(dto);
        return new ResponseEntity<>(createdPhanHoi, HttpStatus.CREATED);
    }

    @GetMapping("/yeu-cau/{idYeuCau}")
    public ResponseEntity<List<PhanHoiYeuCauDTO>> getPhanHoiByYeuCauId(@PathVariable Long idYeuCau) {
        List<PhanHoiYeuCauDTO> phanHoiList = phanHoiYeuCauService.getPhanHoiByYeuCauId(idYeuCau);
        return ResponseEntity.ok(phanHoiList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhanHoiYeuCauDTO> capNhatPhanHoi(
            @PathVariable Long id,
            @Valid @RequestBody PhanHoiYeuCauDTO dto,
            Authentication authentication) {
        
        Long idNguoiCapNhat = Long.parseLong(authentication.getName());
        PhanHoiYeuCauDTO updatedPhanHoi = phanHoiYeuCauService.capNhatPhanHoi(id, dto, idNguoiCapNhat);
        return ResponseEntity.ok(updatedPhanHoi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaPhanHoi(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long idNguoiXoa = Long.parseLong(authentication.getName());
        phanHoiYeuCauService.xoaPhanHoi(id, idNguoiXoa);
        return ResponseEntity.noContent().build();
    }
}
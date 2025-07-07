package com.audistore.audi.controller;

import com.audistore.audi.dto.HinhAnhTheoNoiThatDTO;
import com.audistore.audi.service.HinhAnhTheoNoiThatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hinh-anh-theo-noi-that")
public class HinhAnhTheoNoiThatController {

    @Autowired
    private HinhAnhTheoNoiThatService hinhAnhTheoNoiThatService;

    @GetMapping("/mau-xe/{mauXeId}/noi-that/{noiThatId}")
    public ResponseEntity<List<HinhAnhTheoNoiThatDTO>> getHinhAnhTheoNoiThat(
            @PathVariable Long mauXeId,
            @PathVariable Long noiThatId,
            @RequestParam(value = "mauSacNgoaiId", required = false) Long mauSacNgoaiId) {
        return ResponseEntity.ok(hinhAnhTheoNoiThatService.getHinhAnhTheoNoiThat(mauXeId, noiThatId, mauSacNgoaiId));
    }

    @GetMapping("/mau-xe/{mauXeId}/noi-that/{noiThatId}/loai/{loaiHinh}")
    public ResponseEntity<List<HinhAnhTheoNoiThatDTO>> getHinhAnhTheoNoiThatVaLoai(
            @PathVariable Long mauXeId,
            @PathVariable Long noiThatId,
            @PathVariable String loaiHinh) {
        return ResponseEntity.ok(hinhAnhTheoNoiThatService.getHinhAnhTheoNoiThatVaLoai(mauXeId, noiThatId, loaiHinh));
    }

    @PostMapping
    public ResponseEntity<HinhAnhTheoNoiThatDTO> createHinhAnhTheoNoiThat(
            @RequestBody HinhAnhTheoNoiThatDTO dto) {
        return new ResponseEntity<>(hinhAnhTheoNoiThatService.createHinhAnhTheoNoiThat(dto), HttpStatus.CREATED);
    }
}
// backend/audi/src/main/java/com/audistore/audi/controller/HinhAnhXeTheoMauController.java
package com.audistore.audi.controller;

import com.audistore.audi.dto.HinhAnhXeTheoMauDTO;
import com.audistore.audi.service.HinhAnhXeTheoMauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hinh-anh-theo-mau")
public class HinhAnhXeTheoMauController {

    @Autowired
    private HinhAnhXeTheoMauService hinhAnhXeTheoMauService;

    @GetMapping("/mau-xe/{idMauXe}")
    public ResponseEntity<List<HinhAnhXeTheoMauDTO>> getHinhAnhByMauXe(@PathVariable Long idMauXe) {
        return ResponseEntity.ok(hinhAnhXeTheoMauService.getHinhAnhByMauXe(idMauXe));
    }

    @GetMapping("/mau-xe/{idMauXe}/mau-sac/{idMauSac}")
    public ResponseEntity<List<HinhAnhXeTheoMauDTO>> getHinhAnhByMauXeAndMauSac(
            @PathVariable Long idMauXe,
            @PathVariable Long idMauSac) {
        return ResponseEntity.ok(hinhAnhXeTheoMauService.getHinhAnhByMauXeAndMauSac(idMauXe, idMauSac));
    }

    @GetMapping("/mau-xe/{idMauXe}/mau-sac/{idMauSac}/loai/{loaiHinh}")
    public ResponseEntity<List<HinhAnhXeTheoMauDTO>> getHinhAnhByMauXeAndMauSacAndLoai(
            @PathVariable Long idMauXe,
            @PathVariable Long idMauSac,
            @PathVariable String loaiHinh) {
        return ResponseEntity.ok(hinhAnhXeTheoMauService.getHinhAnhByMauXeAndMauSacAndLoai(idMauXe, idMauSac, loaiHinh));
    }
}
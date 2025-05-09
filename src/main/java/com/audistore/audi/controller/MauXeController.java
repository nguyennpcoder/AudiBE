package com.audistore.audi.controller;

import com.audistore.audi.dto.MauXeDTO;
import com.audistore.audi.service.MauXeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mau-xe")
public class MauXeController {

    private final MauXeService mauXeService;

    @Autowired
    public MauXeController(MauXeService mauXeService) {
        this.mauXeService = mauXeService;
    }

    @GetMapping
    public ResponseEntity<List<MauXeDTO>> getAllMauXe() {
        return ResponseEntity.ok(mauXeService.getAllMauXe());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MauXeDTO> getMauXeById(@PathVariable Long id) {
        return ResponseEntity.ok(mauXeService.getMauXeById(id));
    }

    @GetMapping("/dong-xe/{dongXeId}")
    public ResponseEntity<List<MauXeDTO>> getMauXeByDongXe(@PathVariable Long dongXeId) {
        return ResponseEntity.ok(mauXeService.getMauXeByDongXe(dongXeId));
    }

    @GetMapping("/nam-san-xuat/{namSanXuat}")
    public ResponseEntity<List<MauXeDTO>> getMauXeByNamSanXuat(@PathVariable Integer namSanXuat) {
        return ResponseEntity.ok(mauXeService.getMauXeByNamSanXuat(namSanXuat));
    }

    @GetMapping("/con-hang")
    public ResponseEntity<List<MauXeDTO>> getMauXeConHang(@RequestParam(defaultValue = "true") Boolean conHang) {
        return ResponseEntity.ok(mauXeService.getMauXeByConHang(conHang));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MauXeDTO>> searchMauXe(@RequestParam String keyword) {
        return ResponseEntity.ok(mauXeService.searchMauXe(keyword));
    }

    @GetMapping("/gia")
    public ResponseEntity<List<MauXeDTO>> getMauXeByGiaTrongKhoang(
            @RequestParam BigDecimal giaTu,
            @RequestParam BigDecimal giaDen) {
        return ResponseEntity.ok(mauXeService.getMauXeByGiaTrongKhoang(giaTu, giaDen));
    }

    @GetMapping("/phan-loai/{phanLoai}")
    public ResponseEntity<List<MauXeDTO>> getMauXeByPhanLoai(@PathVariable String phanLoai) {
        return ResponseEntity.ok(mauXeService.getMauXeByPhanLoai(phanLoai));
    }

    @PostMapping
    public ResponseEntity<MauXeDTO> createMauXe(@Valid @RequestBody MauXeDTO mauXeDTO) {
        return new ResponseEntity<>(mauXeService.createMauXe(mauXeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MauXeDTO> updateMauXe(@PathVariable Long id, @Valid @RequestBody MauXeDTO mauXeDTO) {
        return ResponseEntity.ok(mauXeService.updateMauXe(id, mauXeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMauXe(@PathVariable Long id) {
        mauXeService.deleteMauXe(id);
        return ResponseEntity.noContent().build();
    }
}
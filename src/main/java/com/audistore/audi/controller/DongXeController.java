package com.audistore.audi.controller;

import com.audistore.audi.dto.DongXeDTO;
import com.audistore.audi.service.DongXeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dong-xe")
public class DongXeController {

    private final DongXeService dongXeService;

    @Autowired
    public DongXeController(DongXeService dongXeService) {
        this.dongXeService = dongXeService;
    }

    @GetMapping
    public ResponseEntity<List<DongXeDTO>> getAllDongXe() {
        return ResponseEntity.ok(dongXeService.getAllDongXe());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DongXeDTO> getDongXeById(@PathVariable Long id) {
        return ResponseEntity.ok(dongXeService.getDongXeById(id));
    }

    @GetMapping("/phan-loai/{phanLoai}")
    public ResponseEntity<List<DongXeDTO>> getDongXeByPhanLoai(@PathVariable String phanLoai) {
        return ResponseEntity.ok(dongXeService.getDongXeByPhanLoai(phanLoai));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DongXeDTO>> searchDongXe(@RequestParam String keyword) {
        return ResponseEntity.ok(dongXeService.searchDongXe(keyword));
    }

    @PostMapping
    public ResponseEntity<DongXeDTO> createDongXe(@Valid @RequestBody DongXeDTO dongXeDTO) {
        return new ResponseEntity<>(dongXeService.createDongXe(dongXeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DongXeDTO> updateDongXe(@PathVariable Long id, @Valid @RequestBody DongXeDTO dongXeDTO) {
        return ResponseEntity.ok(dongXeService.updateDongXe(id, dongXeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDongXe(@PathVariable Long id) {
        dongXeService.deleteDongXe(id);
        return ResponseEntity.noContent().build();
    }
}
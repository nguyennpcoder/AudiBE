package com.audistore.audi.controller;

import com.audistore.audi.dto.DaiLyDTO;
import com.audistore.audi.service.DaiLyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dai-ly")
public class DaiLyController {

    private final DaiLyService daiLyService;

    @Autowired
    public DaiLyController(DaiLyService daiLyService) {
        this.daiLyService = daiLyService;
    }

    @GetMapping
    public ResponseEntity<List<DaiLyDTO>> getAllDaiLy() {
        return ResponseEntity.ok(daiLyService.getAllDaiLy());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DaiLyDTO> getDaiLyById(@PathVariable Long id) {
        return ResponseEntity.ok(daiLyService.getDaiLyById(id));
    }

    @GetMapping("/thanh-pho/{thanhPho}")
    public ResponseEntity<List<DaiLyDTO>> getDaiLyByThanhPho(@PathVariable String thanhPho) {
        return ResponseEntity.ok(daiLyService.getDaiLyByThanhPho(thanhPho));
    }

    @GetMapping("/tinh/{tinh}")
    public ResponseEntity<List<DaiLyDTO>> getDaiLyByTinh(@PathVariable String tinh) {
        return ResponseEntity.ok(daiLyService.getDaiLyByTinh(tinh));
    }

    @GetMapping("/dich-vu")
    public ResponseEntity<List<DaiLyDTO>> getDaiLyDichVu() {
        return ResponseEntity.ok(daiLyService.getDaiLyDichVu());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DaiLyDTO>> searchDaiLy(@RequestParam String keyword) {
        return ResponseEntity.ok(daiLyService.searchDaiLy(keyword));
    }

    @GetMapping("/mau-xe/{mauXeId}")
    public ResponseEntity<List<DaiLyDTO>> getDaiLyCoMauXe(@PathVariable Long mauXeId) {
        return ResponseEntity.ok(daiLyService.getDaiLyCoMauXe(mauXeId));
    }

    @PostMapping
    public ResponseEntity<DaiLyDTO> createDaiLy(@Valid @RequestBody DaiLyDTO daiLyDTO) {
        return new ResponseEntity<>(daiLyService.createDaiLy(daiLyDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DaiLyDTO> updateDaiLy(@PathVariable Long id, @Valid @RequestBody DaiLyDTO daiLyDTO) {
        return ResponseEntity.ok(daiLyService.updateDaiLy(id, daiLyDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDaiLy(@PathVariable Long id) {
        daiLyService.deleteDaiLy(id);
        return ResponseEntity.noContent().build();
    }
}
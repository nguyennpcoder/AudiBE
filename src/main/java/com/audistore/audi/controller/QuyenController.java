package com.audistore.audi.controller;

import com.audistore.audi.dto.QuyenDTO;
import com.audistore.audi.service.QuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quyen")
public class QuyenController {

    @Autowired
    private QuyenService quyenService;

    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<List<QuyenDTO>> getAllQuyen() {
        return ResponseEntity.ok(quyenService.getAllQuyen());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<QuyenDTO> getQuyenById(@PathVariable Long id) {
        return ResponseEntity.ok(quyenService.getQuyenById(id));
    }

    @GetMapping("/ma/{maQuyen}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<QuyenDTO> getQuyenByMaQuyen(@PathVariable String maQuyen) {
        return ResponseEntity.ok(quyenService.getQuyenByMaQuyen(maQuyen));
    }

    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<QuyenDTO> createQuyen(@RequestBody QuyenDTO quyenDTO) {
        return new ResponseEntity<>(quyenService.createQuyen(quyenDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<QuyenDTO> updateQuyen(@PathVariable Long id, @RequestBody QuyenDTO quyenDTO) {
        return ResponseEntity.ok(quyenService.updateQuyen(id, quyenDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteQuyen(@PathVariable Long id) {
        quyenService.deleteQuyen(id);
        return ResponseEntity.noContent().build();
    }
}
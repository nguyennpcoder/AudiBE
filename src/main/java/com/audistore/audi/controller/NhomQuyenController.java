package com.audistore.audi.controller;

import com.audistore.audi.dto.NhomQuyenDTO;
import com.audistore.audi.service.NhomQuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/nhom-quyen")
public class NhomQuyenController {

    @Autowired
    private NhomQuyenService nhomQuyenService;

    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<List<NhomQuyenDTO>> getAllNhomQuyen() {
        return ResponseEntity.ok(nhomQuyenService.getAllNhomQuyen());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<NhomQuyenDTO> getNhomQuyenById(@PathVariable Long id) {
        return ResponseEntity.ok(nhomQuyenService.getNhomQuyenById(id));
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Set<NhomQuyenDTO>> getNhomQuyenByNguoiDungId(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(nhomQuyenService.getNhomQuyenByNguoiDungId(idNguoiDung));
    }

    @GetMapping("/quyen/nguoi-dung/{idNguoiDung}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Set<String>> getQuyenByNguoiDungId(@PathVariable Long idNguoiDung) {
        return ResponseEntity.ok(nhomQuyenService.getQuyenByNguoiDungId(idNguoiDung));
    }

    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<NhomQuyenDTO> createNhomQuyen(@RequestBody NhomQuyenDTO nhomQuyenDTO) {
        return new ResponseEntity<>(nhomQuyenService.createNhomQuyen(nhomQuyenDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<NhomQuyenDTO> updateNhomQuyen(@PathVariable Long id, @RequestBody NhomQuyenDTO nhomQuyenDTO) {
        return ResponseEntity.ok(nhomQuyenService.updateNhomQuyen(id, nhomQuyenDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteNhomQuyen(@PathVariable Long id) {
        nhomQuyenService.deleteNhomQuyen(id);
        return ResponseEntity.noContent().build();
    }
}
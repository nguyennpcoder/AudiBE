package com.audistore.audi.controller;

import com.audistore.audi.dto.MauSacDTO;
import com.audistore.audi.service.MauSacService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mau-sac")
public class MauSacController {

    private final MauSacService mauSacService;

    @Autowired
    public MauSacController(MauSacService mauSacService) {
        this.mauSacService = mauSacService;
    }

    @GetMapping
    public ResponseEntity<List<MauSacDTO>> getAllMauSac() {
        return ResponseEntity.ok(mauSacService.getAllMauSac());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MauSacDTO> getMauSacById(@PathVariable Long id) {
        return ResponseEntity.ok(mauSacService.getMauSacById(id));
    }

    @GetMapping("/mau-xe/{mauXeId}")
    public ResponseEntity<List<MauSacDTO>> getMauSacByMauXe(@PathVariable Long mauXeId) {
        return ResponseEntity.ok(mauSacService.getMauSacByMauXe(mauXeId));
    }

    @PostMapping
    public ResponseEntity<MauSacDTO> createMauSac(@Valid @RequestBody MauSacDTO mauSacDTO) {
        return new ResponseEntity<>(mauSacService.createMauSac(mauSacDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MauSacDTO> updateMauSac(@PathVariable Long id, @Valid @RequestBody MauSacDTO mauSacDTO) {
        return ResponseEntity.ok(mauSacService.updateMauSac(id, mauSacDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMauSac(@PathVariable Long id) {
        mauSacService.deleteMauSac(id);
        return ResponseEntity.noContent().build();
    }
}
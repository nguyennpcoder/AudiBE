package com.audistore.audi.controller;

import com.audistore.audi.dto.KiemTraTonKhoDTO;
import com.audistore.audi.dto.TonKhoDTO;
import com.audistore.audi.service.TonKhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ton-kho")
public class TonKhoController {

    private final TonKhoService tonKhoService;

    @Autowired
    public TonKhoController(TonKhoService tonKhoService) {
        this.tonKhoService = tonKhoService;
    }

    @GetMapping
    public ResponseEntity<List<TonKhoDTO>> getAllTonKho() {
        return ResponseEntity.ok(tonKhoService.getAllTonKho());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TonKhoDTO> getTonKhoById(@PathVariable Long id) {
        return ResponseEntity.ok(tonKhoService.getTonKhoById(id));
    }

    @GetMapping("/so-khung/{soKhung}")
    public ResponseEntity<TonKhoDTO> getTonKhoBySoKhung(@PathVariable String soKhung) {
        return ResponseEntity.ok(tonKhoService.getTonKhoBySoKhung(soKhung));
    }

    @GetMapping("/mau-xe/{mauXeId}")
    public ResponseEntity<List<TonKhoDTO>> getTonKhoByMauXe(@PathVariable Long mauXeId) {
        return ResponseEntity.ok(tonKhoService.getTonKhoByMauXe(mauXeId));
    }

    @GetMapping("/dai-ly/{daiLyId}")
    public ResponseEntity<List<TonKhoDTO>> getTonKhoByDaiLy(@PathVariable Long daiLyId) {
        return ResponseEntity.ok(tonKhoService.getTonKhoByDaiLy(daiLyId));
    }

    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<TonKhoDTO>> getTonKhoByTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(tonKhoService.getTonKhoByTrangThai(trangThai));
    }

    @GetMapping("/co-san")
    public ResponseEntity<List<TonKhoDTO>> getXeCoSanTaiDaiLy(
            @RequestParam Long mauXeId,
            @RequestParam Long daiLyId) {
        return ResponseEntity.ok(tonKhoService.getXeCoSanTaiDaiLy(mauXeId, daiLyId));
    }

    @GetMapping("/kiem-tra/{mauXeId}")
    public ResponseEntity<KiemTraTonKhoDTO> kiemTraTonKho(@PathVariable Long mauXeId) {
        return ResponseEntity.ok(tonKhoService.kiemTraTonKho(mauXeId));
    }

    @PostMapping
    public ResponseEntity<TonKhoDTO> createTonKho(@Valid @RequestBody TonKhoDTO tonKhoDTO) {
        return new ResponseEntity<>(tonKhoService.createTonKho(tonKhoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TonKhoDTO> updateTonKho(@PathVariable Long id, @Valid @RequestBody TonKhoDTO tonKhoDTO) {
        return ResponseEntity.ok(tonKhoService.updateTonKho(id, tonKhoDTO));
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<TonKhoDTO> capNhatTrangThaiTonKho(
            @PathVariable Long id,
            @RequestParam String trangThai) {
        return ResponseEntity.ok(tonKhoService.capNhatTrangThaiTonKho(id, trangThai));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTonKho(@PathVariable Long id) {
        tonKhoService.deleteTonKho(id);
        return ResponseEntity.noContent().build();
    }
}
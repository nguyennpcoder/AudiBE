package com.audistore.audi.controller;

import com.audistore.audi.dto.HinhAnhXeDTO;
import com.audistore.audi.dto.UploadImageResponseDTO;
import com.audistore.audi.service.HinhAnhXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hinh-anh")
public class HinhAnhXeController {
    
    @Autowired
    private HinhAnhXeService hinhAnhXeService;
    
    @GetMapping
    public ResponseEntity<List<HinhAnhXeDTO>> getAllHinhAnhXe() {
        return ResponseEntity.ok(hinhAnhXeService.getAllHinhAnhXe());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HinhAnhXeDTO> getHinhAnhById(@PathVariable Long id) {
        return ResponseEntity.ok(hinhAnhXeService.getHinhAnhById(id));
    }
    
    @GetMapping("/mau-xe/{idMauXe}")
    public ResponseEntity<List<HinhAnhXeDTO>> getHinhAnhByMauXe(@PathVariable Long idMauXe) {
        return ResponseEntity.ok(hinhAnhXeService.getHinhAnhByMauXe(idMauXe));
    }
    
    @GetMapping("/mau-xe/{idMauXe}/loai/{loaiHinh}")
    public ResponseEntity<List<HinhAnhXeDTO>> getHinhAnhByMauXeAndLoai(
            @PathVariable Long idMauXe,
            @PathVariable String loaiHinh) {
        return ResponseEntity.ok(hinhAnhXeService.getHinhAnhByMauXeAndLoai(idMauXe, loaiHinh));
    }
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<UploadImageResponseDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idMauXe") Long idMauXe,
            @RequestParam("loaiHinh") String loaiHinh,
            @RequestParam(value = "viTri", required = false) Integer viTri) {
        
        UploadImageResponseDTO response = hinhAnhXeService.uploadImage(file, idMauXe, loaiHinh, viTri);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<List<UploadImageResponseDTO>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("idMauXe") Long idMauXe,
            @RequestParam("loaiHinh") String loaiHinh) {
        
        List<UploadImageResponseDTO> responses = hinhAnhXeService.uploadMultipleImages(files, idMauXe, loaiHinh);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<HinhAnhXeDTO> updateHinhAnh(
            @PathVariable Long id,
            @RequestBody HinhAnhXeDTO hinhAnhXeDTO) {
        
        return ResponseEntity.ok(hinhAnhXeService.updateHinhAnh(id, hinhAnhXeDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteHinhAnh(@PathVariable Long id) {
        hinhAnhXeService.deleteHinhAnh(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/mau-xe/{idMauXe}")
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteAllHinhAnhByMauXe(@PathVariable Long idMauXe) {
        hinhAnhXeService.deleteAllHinhAnhByMauXe(idMauXe);
        return ResponseEntity.noContent().build();
    }
}
package com.audistore.audi.controller;

import com.audistore.audi.dto.UploadImageResponseDTO;
import com.audistore.audi.service.MauSacImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/mau-sac-image")
public class MauSacImageController {
    
    @Autowired
    private MauSacImageService mauSacImageService;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<UploadImageResponseDTO> uploadColorImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idMauSac") Long idMauSac) {
        
        UploadImageResponseDTO response = mauSacImageService.uploadColorImage(file, idMauSac);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{idMauSac}")
    @PreAuthorize("hasAnyRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteColorImage(@PathVariable Long idMauSac) {
        mauSacImageService.deleteColorImage(idMauSac);
        return ResponseEntity.noContent().build();
    }
}
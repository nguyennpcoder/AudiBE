package com.audistore.audi.controller;

import com.audistore.audi.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String filePath = fileStorageService.storeFile(file);
        return ResponseEntity.ok(Map.of("url", filePath));
    }
} 
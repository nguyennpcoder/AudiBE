package com.audistore.audi.service;

import com.audistore.audi.dto.UploadImageResponseDTO;
import com.audistore.audi.model.MauSac;
import com.audistore.audi.repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class MauSacImageService {
    
    @Autowired
    private MauSacRepository mauSacRepository;
    
    private final Path rootLocation = Paths.get("uploads/images/colors");
    
    public MauSacImageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ cho màu sắc", e);
        }
    }
    
    @Transactional
    public UploadImageResponseDTO uploadColorImage(MultipartFile file, Long idMauSac) {
        // Validate màu sắc
        MauSac mauSac = mauSacRepository.findById(idMauSac)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + idMauSac));
        
        // Xử lý file
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String storageFileName = "color_" + idMauSac + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
        
        // Tạo đường dẫn đầy đủ
        Path targetLocation = rootLocation.resolve(storageFileName);
        
        try {
            // Lưu file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Cập nhật đường dẫn ảnh trong entity màu sắc
            String imagePath = "/uploads/images/colors/" + storageFileName;
            mauSac.setDuongDanAnh(imagePath);
            mauSacRepository.save(mauSac);
            
            // Tạo response
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/images/colors/")
                    .path(storageFileName)
                    .toUriString();
            
            return new UploadImageResponseDTO(
                    originalFileName,
                    fileDownloadUri,
                    file.getContentType(),
                    file.getSize()
            );
            
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu trữ file " + originalFileName, e);
        }
    }
    
    @Transactional
    public void deleteColorImage(Long idMauSac) {
        MauSac mauSac = mauSacRepository.findById(idMauSac)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + idMauSac));
        
        // Xóa file vật lý nếu tồn tại
        String filePath = mauSac.getDuongDanAnh();
        if (filePath != null && !filePath.isEmpty()) {
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            Path file = rootLocation.resolve(fileName);
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                // Chỉ log lỗi, không dừng quá trình xóa
                System.err.println("Không thể xóa file " + fileName + ": " + e.getMessage());
            }
        }
        
        // Xóa đường dẫn ảnh
        mauSac.setDuongDanAnh(null);
        mauSacRepository.save(mauSac);
    }
}
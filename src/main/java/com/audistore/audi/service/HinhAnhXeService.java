package com.audistore.audi.service;

import com.audistore.audi.dto.HinhAnhXeDTO;
import com.audistore.audi.dto.UploadImageResponseDTO;
import com.audistore.audi.model.HinhAnhXe;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.repository.HinhAnhXeRepository;
import com.audistore.audi.repository.MauXeRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HinhAnhXeService {
    
    @Autowired
    private HinhAnhXeRepository hinhAnhXeRepository;
    
    @Autowired
    private MauXeRepository mauXeRepository;
    
    private final Path rootLocation = Paths.get("uploads/images/vehicles");
    
    public HinhAnhXeService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ", e);
        }
    }
    
    public List<HinhAnhXeDTO> getAllHinhAnhXe() {
        return hinhAnhXeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HinhAnhXeDTO> getHinhAnhByMauXe(Long idMauXe) {
        return hinhAnhXeRepository.findByMauXeId(idMauXe).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HinhAnhXeDTO> getHinhAnhByMauXeAndLoai(Long idMauXe, String loaiHinh) {
        HinhAnhXe.LoaiHinh loai = HinhAnhXe.LoaiHinh.valueOf(loaiHinh);
        return hinhAnhXeRepository.findByMauXeIdAndLoaiHinh(idMauXe, loai).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public HinhAnhXeDTO getHinhAnhById(Long id) {
        HinhAnhXe hinhAnh = hinhAnhXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh với ID: " + id));
        return mapToDTO(hinhAnh);
    }
    
    @Transactional
    public UploadImageResponseDTO uploadImage(MultipartFile file, Long idMauXe, String loaiHinh, Integer viTri) {
        // Validate mẫu xe
        MauXe mauXe = mauXeRepository.findById(idMauXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + idMauXe));
        
        // Validate loại hình
        HinhAnhXe.LoaiHinh loai;
        try {
            loai = HinhAnhXe.LoaiHinh.valueOf(loaiHinh);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Loại hình không hợp lệ. Các loại hợp lệ: ngoai_that, noi_that, chi_tiet, tinh_nang, thu_nho");
        }
        
        // Xử lý file
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String storageFileName = idMauXe + "_" + loaiHinh + "_" + (viTri != null ? viTri : System.currentTimeMillis()) + fileExtension;
        
        // Tạo đường dẫn đầy đủ
        Path targetLocation = rootLocation.resolve(storageFileName);
        
        try {
            // Lưu file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Tạo bản ghi trong database
            HinhAnhXe hinhAnhXe = new HinhAnhXe();
            hinhAnhXe.setMauXe(mauXe);
            hinhAnhXe.setDuongDanAnh("/uploads/images/vehicles/" + storageFileName);
            hinhAnhXe.setLoaiHinh(loai);
            hinhAnhXe.setViTri(viTri);
            
            hinhAnhXeRepository.save(hinhAnhXe);
            
            // Tạo response
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/images/vehicles/")
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
    public List<UploadImageResponseDTO> uploadMultipleImages(List<MultipartFile> files, Long idMauXe, String loaiHinh) {
        return files.stream()
                .map(file -> uploadImage(file, idMauXe, loaiHinh, null))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public HinhAnhXeDTO updateHinhAnh(Long id, HinhAnhXeDTO hinhAnhXeDTO) {
        HinhAnhXe hinhAnh = hinhAnhXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh với ID: " + id));
        
        // Chỉ cập nhật vị trí và loại hình, không thay đổi đường dẫn ảnh và mẫu xe
        if (hinhAnhXeDTO.getLoaiHinh() != null) {
            hinhAnh.setLoaiHinh(HinhAnhXe.LoaiHinh.valueOf(hinhAnhXeDTO.getLoaiHinh()));
        }
        
        if (hinhAnhXeDTO.getViTri() != null) {
            hinhAnh.setViTri(hinhAnhXeDTO.getViTri());
        }
        
        HinhAnhXe updatedHinhAnh = hinhAnhXeRepository.save(hinhAnh);
        return mapToDTO(updatedHinhAnh);
    }
    
    @Transactional
    public void deleteHinhAnh(Long id) {
        HinhAnhXe hinhAnh = hinhAnhXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh với ID: " + id));
        
        // Xóa file vật lý nếu tồn tại
        String filePath = hinhAnh.getDuongDanAnh();
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
        
        // Xóa bản ghi từ database
        hinhAnhXeRepository.deleteById(id);
    }
    
    @Transactional
    public void deleteAllHinhAnhByMauXe(Long idMauXe) {
        List<HinhAnhXe> danhSachHinh = hinhAnhXeRepository.findByMauXeId(idMauXe);
        
        // Xóa tất cả file vật lý
        for (HinhAnhXe hinhAnh : danhSachHinh) {
            String filePath = hinhAnh.getDuongDanAnh();
            if (filePath != null && !filePath.isEmpty()) {
                String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                Path file = rootLocation.resolve(fileName);
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    System.err.println("Không thể xóa file " + fileName + ": " + e.getMessage());
                }
            }
        }
        
        // Xóa tất cả bản ghi từ database
        hinhAnhXeRepository.deleteByMauXeId(idMauXe);
    }
    
    private HinhAnhXeDTO mapToDTO(HinhAnhXe hinhAnh) {
        HinhAnhXeDTO dto = new HinhAnhXeDTO();
        dto.setId(hinhAnh.getId());
        dto.setIdMauXe(hinhAnh.getMauXe().getId());
        dto.setDuongDanAnh(hinhAnh.getDuongDanAnh());
        dto.setLoaiHinh(hinhAnh.getLoaiHinh().name());
        dto.setViTri(hinhAnh.getViTri());
        dto.setTenMauXe(hinhAnh.getMauXe().getTenMau());
        return dto;
    }
}
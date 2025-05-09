package com.audistore.audi.service;

import com.audistore.audi.dto.DangKyNhanTinDTO;
import com.audistore.audi.model.DangKyNhanTin;
import com.audistore.audi.repository.DangKyNhanTinRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DangKyNhanTinService {
    
    private final DangKyNhanTinRepository dangKyNhanTinRepository;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    public DangKyNhanTinService(DangKyNhanTinRepository dangKyNhanTinRepository, ObjectMapper objectMapper) {
        this.dangKyNhanTinRepository = dangKyNhanTinRepository;
        this.objectMapper = objectMapper;
    }
    
    public List<DangKyNhanTinDTO> getAllDangKyNhanTin() {
        return dangKyNhanTinRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DangKyNhanTinDTO> getDangKyNhanTinActive() {
        return dangKyNhanTinRepository.findByDangHoatDong(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public DangKyNhanTinDTO getDangKyNhanTinById(Long id) {
        return dangKyNhanTinRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký nhận tin với ID: " + id));
    }
    
    public DangKyNhanTinDTO getDangKyNhanTinByEmail(String email) {
        return dangKyNhanTinRepository.findByEmail(email)
                .map(this::mapToDTO)
                .orElse(null);
    }
    
    public DangKyNhanTinDTO createDangKyNhanTin(DangKyNhanTinDTO dangKyNhanTinDTO) {
        // Kiểm tra email đã tồn tại chưa
        Optional<DangKyNhanTin> existingSubscription = dangKyNhanTinRepository.findByEmail(dangKyNhanTinDTO.getEmail());
        
        if (existingSubscription.isPresent()) {
            DangKyNhanTin existing = existingSubscription.get();
            
            // Nếu email đã đăng ký và vẫn đang hoạt động
            if (existing.getDangHoatDong()) {
                throw new RuntimeException("Email đã đăng ký nhận tin");
            } 
            // Nếu email đã hủy đăng ký trước đó, kích hoạt lại
            else {
                existing.setDangHoatDong(true);
                existing.setHoTen(dangKyNhanTinDTO.getHoTen());
                
                // Cập nhật quan tâm mới
                try {
                    if (dangKyNhanTinDTO.getQuanTam() != null && !dangKyNhanTinDTO.getQuanTam().isEmpty()) {
                        existing.setQuanTam(objectMapper.writeValueAsString(dangKyNhanTinDTO.getQuanTam()));
                    }
                } catch (JsonProcessingException e) {
                    // Giữ nguyên giá trị quan tâm cũ nếu xảy ra lỗi
                }
                
                DangKyNhanTinDTO result = mapToDTO(dangKyNhanTinRepository.save(existing));
                result.setResubscribed(true); // Thêm flag để đánh dấu đây là đăng ký lại
                return result;
            }
        }
        
        // Trường hợp email chưa đăng ký, tạo mới
        DangKyNhanTin dangKyNhanTin = mapToEntity(dangKyNhanTinDTO);
        return mapToDTO(dangKyNhanTinRepository.save(dangKyNhanTin));
    }
    
    public DangKyNhanTinDTO updateDangKyNhanTin(Long id, DangKyNhanTinDTO dangKyNhanTinDTO) {
        DangKyNhanTin existingDangKy = dangKyNhanTinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký nhận tin với ID: " + id));
        
        // Cập nhật thông tin
        updateEntityFromDTO(existingDangKy, dangKyNhanTinDTO);
        
        return mapToDTO(dangKyNhanTinRepository.save(existingDangKy));
    }
    
    public void deleteDangKyNhanTin(Long id) {
        dangKyNhanTinRepository.deleteById(id);
    }
    
    public DangKyNhanTinDTO unsubscribe(String email) {
        DangKyNhanTin dangKyNhanTin = dangKyNhanTinRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email chưa đăng ký nhận tin"));
        
        dangKyNhanTin.setDangHoatDong(false);
        return mapToDTO(dangKyNhanTinRepository.save(dangKyNhanTin));
    }
    
    private DangKyNhanTinDTO mapToDTO(DangKyNhanTin dangKyNhanTin) {
        DangKyNhanTinDTO dto = new DangKyNhanTinDTO();
        dto.setId(dangKyNhanTin.getId());
        dto.setEmail(dangKyNhanTin.getEmail());
        dto.setHoTen(dangKyNhanTin.getHoTen());
        dto.setDangHoatDong(dangKyNhanTin.getDangHoatDong());
        dto.setNgayDangKy(dangKyNhanTin.getNgayDangKy());
        
        // Chuyển đổi JSON thành List<String>
        try {
            if (dangKyNhanTin.getQuanTam() != null && !dangKyNhanTin.getQuanTam().isEmpty()) {
                dto.setQuanTam(objectMapper.readValue(dangKyNhanTin.getQuanTam(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
            } else {
                dto.setQuanTam(new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            dto.setQuanTam(new ArrayList<>());
        }
        
        return dto;
    }
    
    private DangKyNhanTin mapToEntity(DangKyNhanTinDTO dto) {
        DangKyNhanTin dangKyNhanTin = new DangKyNhanTin();
        updateEntityFromDTO(dangKyNhanTin, dto);
        return dangKyNhanTin;
    }
    
    private void updateEntityFromDTO(DangKyNhanTin dangKyNhanTin, DangKyNhanTinDTO dto) {
        dangKyNhanTin.setEmail(dto.getEmail());
        dangKyNhanTin.setHoTen(dto.getHoTen());
        dangKyNhanTin.setDangHoatDong(dto.getDangHoatDong());
        
        // Chuyển đổi List<String> thành JSON
        try {
            if (dto.getQuanTam() != null && !dto.getQuanTam().isEmpty()) {
                dangKyNhanTin.setQuanTam(objectMapper.writeValueAsString(dto.getQuanTam()));
            } else {
                dangKyNhanTin.setQuanTam("[]");
            }
        } catch (JsonProcessingException e) {
            dangKyNhanTin.setQuanTam("[]");
        }
    }
}
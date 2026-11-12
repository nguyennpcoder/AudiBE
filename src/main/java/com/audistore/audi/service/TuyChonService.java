package com.audistore.audi.service;

import com.audistore.audi.dto.TuyChonDTO;
import com.audistore.audi.model.TuyChon;
import com.audistore.audi.repository.TuyChonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TuyChonService {

    @Autowired
    private TuyChonRepository tuyChonRepository;

    public List<TuyChonDTO> getAllTuyChon() {
        try {
            log.info("Bắt đầu lấy tất cả tùy chọn từ database");
            List<TuyChon> tuyChonList = tuyChonRepository.findAll();
            log.info("Đã lấy được {} tùy chọn từ database", tuyChonList.size());
            
            List<TuyChonDTO> result = tuyChonList.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            log.info("Đã chuyển đổi thành công {} tùy chọn sang DTO", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả tùy chọn: ", e);
            throw new RuntimeException("Không thể lấy danh sách tùy chọn: " + e.getMessage(), e);
        }
    }

    public TuyChonDTO getTuyChonById(Long id) {
        try {
            log.info("Đang tìm tùy chọn với ID: {}", id);
            TuyChon tuyChon = tuyChonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tùy chọn với ID: " + id));
            return mapToDTO(tuyChon);
        } catch (Exception e) {
            log.error("Lỗi khi lấy tùy chọn với ID {}: ", id, e);
            throw new RuntimeException("Không thể lấy tùy chọn: " + e.getMessage(), e);
        }
    }

    public List<TuyChonDTO> getTuyChonByDanhMuc(String danhMuc) {
        try {
            log.info("Đang tìm tùy chọn theo danh mục: {}", danhMuc);
            TuyChon.DanhMuc danhMucEnum = TuyChon.DanhMuc.valueOf(danhMuc);
            List<TuyChon> tuyChonList = tuyChonRepository.findByDanhMuc(danhMucEnum);
            log.info("Đã tìm thấy {} tùy chọn cho danh mục: {}", tuyChonList.size(), danhMuc);
            
            return tuyChonList.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
                    
        } catch (IllegalArgumentException e) {
            log.error("Danh mục không hợp lệ: {}", danhMuc);
            throw new RuntimeException("Danh mục không hợp lệ: " + danhMuc);
        } catch (Exception e) {
            log.error("Lỗi khi lấy tùy chọn theo danh mục {}: ", danhMuc, e);
            throw new RuntimeException("Không thể lấy tùy chọn theo danh mục: " + e.getMessage(), e);
        }
    }

    public List<TuyChonDTO> getTuyChonByMauXe(Long mauXeId) {
        try {
            log.info("Đang tìm tùy chọn theo mẫu xe ID: {}", mauXeId);
            List<TuyChon> tuyChonList = tuyChonRepository.findByDanhSachMauXeId(mauXeId);
            log.info("Đã tìm thấy {} tùy chọn cho mẫu xe ID: {}", tuyChonList.size(), mauXeId);
            
            return tuyChonList.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Lỗi khi lấy tùy chọn theo mẫu xe ID {}: ", mauXeId, e);
            throw new RuntimeException("Không thể lấy tùy chọn theo mẫu xe: " + e.getMessage(), e);
        }
    }

    private TuyChonDTO mapToDTO(TuyChon tuyChon) {
        try {
            TuyChonDTO dto = new TuyChonDTO();
            dto.setId(tuyChon.getId());
            
            if (tuyChon.getDanhMuc() != null) {
                dto.setDanhMuc(tuyChon.getDanhMuc().name());
            } else {
                log.warn("Tùy chọn ID {} không có danh mục", tuyChon.getId());
                dto.setDanhMuc("Unknown");
            }
            
            dto.setTen(tuyChon.getTen() != null ? tuyChon.getTen() : "");
            dto.setMoTa(tuyChon.getMoTa());
            dto.setGia(tuyChon.getGia() != null ? tuyChon.getGia() : java.math.BigDecimal.ZERO);
            
            return dto;
            
        } catch (Exception e) {
            log.error("Lỗi khi chuyển đổi TuyChon sang DTO: ", e);
            throw new RuntimeException("Không thể chuyển đổi tùy chọn: " + e.getMessage(), e);
        }
    }
} 
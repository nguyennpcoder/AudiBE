package com.audistore.audi.service;

import com.audistore.audi.dto.DongXeDTO;
import com.audistore.audi.model.DongXe;
import com.audistore.audi.repository.DongXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DongXeService {

    private final DongXeRepository dongXeRepository;

    @Autowired
    public DongXeService(DongXeRepository dongXeRepository) {
        this.dongXeRepository = dongXeRepository;
    }

    @Cacheable(cacheNames = "dongXe:all")
    public List<DongXeDTO> getAllDongXe() {
        return dongXeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "dongXe:byId", key = "#id")
    public DongXeDTO getDongXeById(Long id) {
        return dongXeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng xe với ID: " + id));
    }

    @Cacheable(cacheNames = "dongXe:byPhanLoai", key = "#phanLoai")
    public List<DongXeDTO> getDongXeByPhanLoai(String phanLoai) {
        try {
            DongXe.PhanLoai loai = DongXe.PhanLoai.valueOf(phanLoai);
            return dongXeRepository.findByPhanLoai(loai).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Phân loại không hợp lệ: " + phanLoai);
        }
    }

    @Cacheable(cacheNames = "dongXe:search", key = "#keyword")
    public List<DongXeDTO> searchDongXe(String keyword) {
        return dongXeRepository.findByTenContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = {"dongXe:all", "dongXe:byPhanLoai", "dongXe:search"}, allEntries = true)
    public DongXeDTO createDongXe(DongXeDTO dongXeDTO) {
        if (dongXeRepository.existsByTen(dongXeDTO.getTen())) {
            throw new RuntimeException("Dòng xe với tên này đã tồn tại");
        }
        
        DongXe dongXe = mapToEntity(dongXeDTO);
        DongXe savedDongXe = dongXeRepository.save(dongXe);
        return mapToDTO(savedDongXe);
    }

    @CacheEvict(cacheNames = {"dongXe:all", "dongXe:byId", "dongXe:byPhanLoai", "dongXe:search"}, allEntries = true)
    public DongXeDTO updateDongXe(Long id, DongXeDTO dongXeDTO) {
        DongXe existingDongXe = dongXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng xe với ID: " + id));
        
        updateEntityFromDTO(existingDongXe, dongXeDTO);
        DongXe updatedDongXe = dongXeRepository.save(existingDongXe);
        return mapToDTO(updatedDongXe);
    }

    @CacheEvict(cacheNames = {"dongXe:all", "dongXe:byId", "dongXe:byPhanLoai", "dongXe:search"}, allEntries = true)
    public void deleteDongXe(Long id) {
        if (!dongXeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy dòng xe với ID: " + id);
        }
        dongXeRepository.deleteById(id);
    }

    // Helper methods
    private DongXeDTO mapToDTO(DongXe dongXe) {
        DongXeDTO dto = new DongXeDTO();
        dto.setId(dongXe.getId());
        dto.setTen(dongXe.getTen());
        dto.setMoTa(dongXe.getMoTa());
        dto.setPhanLoai(dongXe.getPhanLoai().name());
        dto.setDuongDanAnh(dongXe.getDuongDanAnh());
        return dto;
    }

    private DongXe mapToEntity(DongXeDTO dto) {
        DongXe dongXe = new DongXe();
        updateEntityFromDTO(dongXe, dto);
        return dongXe;
    }

    private void updateEntityFromDTO(DongXe dongXe, DongXeDTO dto) {
        dongXe.setTen(dto.getTen());
        dongXe.setMoTa(dto.getMoTa());
        dongXe.setPhanLoai(DongXe.PhanLoai.valueOf(dto.getPhanLoai()));
        dongXe.setDuongDanAnh(dto.getDuongDanAnh());
    }
}
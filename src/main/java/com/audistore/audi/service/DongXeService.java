package com.audistore.audi.service;

import com.audistore.audi.dto.DongXeDTO;
import com.audistore.audi.model.DongXe;
import com.audistore.audi.repository.DongXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<DongXeDTO> getAllDongXe() {
        return dongXeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DongXeDTO getDongXeById(Long id) {
        return dongXeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng xe với ID: " + id));
    }

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

    public List<DongXeDTO> searchDongXe(String keyword) {
        return dongXeRepository.findByTenContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DongXeDTO createDongXe(DongXeDTO dongXeDTO) {
        if (dongXeRepository.existsByTen(dongXeDTO.getTen())) {
            throw new RuntimeException("Dòng xe với tên này đã tồn tại");
        }
        
        DongXe dongXe = mapToEntity(dongXeDTO);
        DongXe savedDongXe = dongXeRepository.save(dongXe);
        return mapToDTO(savedDongXe);
    }

    public DongXeDTO updateDongXe(Long id, DongXeDTO dongXeDTO) {
        DongXe existingDongXe = dongXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng xe với ID: " + id));
        
        updateEntityFromDTO(existingDongXe, dongXeDTO);
        DongXe updatedDongXe = dongXeRepository.save(existingDongXe);
        return mapToDTO(updatedDongXe);
    }

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
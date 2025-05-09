package com.audistore.audi.service;

import com.audistore.audi.dto.NhomQuyenDTO;
import com.audistore.audi.dto.QuyenDTO;
import com.audistore.audi.model.NhomQuyen;
import com.audistore.audi.model.Quyen;
import com.audistore.audi.repository.NhomQuyenRepository;
import com.audistore.audi.repository.QuyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class NhomQuyenService {

    @Autowired
    private NhomQuyenRepository nhomQuyenRepository;
    
    @Autowired
    private QuyenRepository quyenRepository;
    
    @Autowired
    private QuyenService quyenService;

    public List<NhomQuyenDTO> getAllNhomQuyen() {
        return nhomQuyenRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NhomQuyenDTO getNhomQuyenById(Long id) {
        return nhomQuyenRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm quyền với ID: " + id));
    }

    public Set<NhomQuyenDTO> getNhomQuyenByNguoiDungId(Long idNguoiDung) {
        return nhomQuyenRepository.findByNguoiDungId(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toSet());
    }

    public Set<String> getQuyenByNguoiDungId(Long idNguoiDung) {
        return nhomQuyenRepository.findQuyenByNguoiDungId(idNguoiDung);
    }

    public NhomQuyenDTO createNhomQuyen(NhomQuyenDTO nhomQuyenDTO) {
        if (nhomQuyenRepository.existsByTen(nhomQuyenDTO.getTen())) {
            throw new RuntimeException("Tên nhóm quyền đã tồn tại: " + nhomQuyenDTO.getTen());
        }
        
        NhomQuyen nhomQuyen = mapToEntity(nhomQuyenDTO);
        NhomQuyen savedNhomQuyen = nhomQuyenRepository.save(nhomQuyen);
        return mapToDTO(savedNhomQuyen);
    }

    public NhomQuyenDTO updateNhomQuyen(Long id, NhomQuyenDTO nhomQuyenDTO) {
        NhomQuyen existingNhomQuyen = nhomQuyenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm quyền với ID: " + id));
                
        if (!existingNhomQuyen.getTen().equals(nhomQuyenDTO.getTen())) {
            if (nhomQuyenRepository.existsByTen(nhomQuyenDTO.getTen())) {
                throw new RuntimeException("Tên nhóm quyền đã tồn tại: " + nhomQuyenDTO.getTen());
            }
        }

        existingNhomQuyen.setTen(nhomQuyenDTO.getTen());
        existingNhomQuyen.setMoTa(nhomQuyenDTO.getMoTa());
        
        // Cập nhật danh sách quyền
        Set<Quyen> quyen = new HashSet<>();
        if (nhomQuyenDTO.getDanhSachQuyen() != null && !nhomQuyenDTO.getDanhSachQuyen().isEmpty()) {
            Set<Long> quyenIds = nhomQuyenDTO.getDanhSachQuyen().stream()
                    .map(QuyenDTO::getId)
                    .collect(Collectors.toSet());
                    
            quyen = quyenRepository.findAllById(quyenIds).stream().collect(Collectors.toSet());
        }
        existingNhomQuyen.setDanhSachQuyen(quyen);
        
        NhomQuyen updatedNhomQuyen = nhomQuyenRepository.save(existingNhomQuyen);
        return mapToDTO(updatedNhomQuyen);
    }

    public void deleteNhomQuyen(Long id) {
        if (!nhomQuyenRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhóm quyền với ID: " + id);
        }
        nhomQuyenRepository.deleteById(id);
    }

    // Helper methods
    public NhomQuyenDTO mapToDTO(NhomQuyen nhomQuyen) {
        NhomQuyenDTO dto = new NhomQuyenDTO();
        dto.setId(nhomQuyen.getId());
        dto.setTen(nhomQuyen.getTen());
        dto.setMoTa(nhomQuyen.getMoTa());
        
        // Map quyền
        if (nhomQuyen.getDanhSachQuyen() != null) {
            Set<QuyenDTO> quyenDTOs = nhomQuyen.getDanhSachQuyen().stream()
                    .map(quyenService::mapToDTO)
                    .collect(Collectors.toSet());
            dto.setDanhSachQuyen(quyenDTOs);
        }
        
        return dto;
    }

    private NhomQuyen mapToEntity(NhomQuyenDTO dto) {
        NhomQuyen nhomQuyen = new NhomQuyen();
        nhomQuyen.setTen(dto.getTen());
        nhomQuyen.setMoTa(dto.getMoTa());
        
        // Map quyền
        if (dto.getDanhSachQuyen() != null && !dto.getDanhSachQuyen().isEmpty()) {
            Set<Long> quyenIds = dto.getDanhSachQuyen().stream()
                    .map(QuyenDTO::getId)
                    .collect(Collectors.toSet());
                    
            Set<Quyen> quyen = quyenRepository.findAllById(quyenIds).stream().collect(Collectors.toSet());
            nhomQuyen.setDanhSachQuyen(quyen);
        }
        
        return nhomQuyen;
    }
}
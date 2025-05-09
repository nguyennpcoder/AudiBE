package com.audistore.audi.service;

import com.audistore.audi.dto.QuyenDTO;
import com.audistore.audi.model.Quyen;
import com.audistore.audi.repository.QuyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuyenService {

    @Autowired
    private QuyenRepository quyenRepository;

    public List<QuyenDTO> getAllQuyen() {
        return quyenRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public QuyenDTO getQuyenById(Long id) {
        return quyenRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền với ID: " + id));
    }

    public QuyenDTO getQuyenByMaQuyen(String maQuyen) {
        return quyenRepository.findByMaQuyen(maQuyen)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền với mã: " + maQuyen));
    }

    public QuyenDTO createQuyen(QuyenDTO quyenDTO) {
        validateQuyenDTO(quyenDTO);
        
        Quyen quyen = mapToEntity(quyenDTO);
        Quyen savedQuyen = quyenRepository.save(quyen);
        return mapToDTO(savedQuyen);
    }

    public QuyenDTO updateQuyen(Long id, QuyenDTO quyenDTO) {
        Quyen existingQuyen = quyenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền với ID: " + id));
                
        // Nếu đang thay đổi mã quyền hoặc tên, cần kiểm tra xem đã có tồn tại chưa
        if (!existingQuyen.getMaQuyen().equals(quyenDTO.getMaQuyen())) {
            if (quyenRepository.existsByMaQuyen(quyenDTO.getMaQuyen())) {
                throw new RuntimeException("Mã quyền đã tồn tại: " + quyenDTO.getMaQuyen());
            }
        }
        
        if (!existingQuyen.getTen().equals(quyenDTO.getTen())) {
            if (quyenRepository.existsByTen(quyenDTO.getTen())) {
                throw new RuntimeException("Tên quyền đã tồn tại: " + quyenDTO.getTen());
            }
        }

        existingQuyen.setTen(quyenDTO.getTen());
        existingQuyen.setMoTa(quyenDTO.getMoTa());
        existingQuyen.setMaQuyen(quyenDTO.getMaQuyen());
        
        Quyen updatedQuyen = quyenRepository.save(existingQuyen);
        return mapToDTO(updatedQuyen);
    }

    public void deleteQuyen(Long id) {
        if (!quyenRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy quyền với ID: " + id);
        }
        quyenRepository.deleteById(id);
    }
    
    private void validateQuyenDTO(QuyenDTO quyenDTO) {
        if (quyenRepository.existsByMaQuyen(quyenDTO.getMaQuyen())) {
            throw new RuntimeException("Mã quyền đã tồn tại: " + quyenDTO.getMaQuyen());
        }
        
        if (quyenRepository.existsByTen(quyenDTO.getTen())) {
            throw new RuntimeException("Tên quyền đã tồn tại: " + quyenDTO.getTen());
        }
    }

    // Helper methods
    public QuyenDTO mapToDTO(Quyen quyen) {
        QuyenDTO dto = new QuyenDTO();
        dto.setId(quyen.getId());
        dto.setTen(quyen.getTen());
        dto.setMoTa(quyen.getMoTa());
        dto.setMaQuyen(quyen.getMaQuyen());
        return dto;
    }

    private Quyen mapToEntity(QuyenDTO dto) {
        Quyen quyen = new Quyen();
        quyen.setTen(dto.getTen());
        quyen.setMoTa(dto.getMoTa());
        quyen.setMaQuyen(dto.getMaQuyen());
        return quyen;
    }
}
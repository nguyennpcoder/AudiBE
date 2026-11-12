package com.audistore.audi.service;

import com.audistore.audi.dto.BanhXeDTO;
import com.audistore.audi.model.BanhXe;
import com.audistore.audi.repository.BanhXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BanhXeService {

    private final BanhXeRepository banhXeRepository;

    @Autowired
    public BanhXeService(BanhXeRepository banhXeRepository) {
        this.banhXeRepository = banhXeRepository;
    }

    public List<BanhXeDTO> getAllBanhXe() {
        return banhXeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BanhXeDTO getBanhXeById(Long id) {
        return banhXeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bánh xe với ID: " + id));
    }

    public List<BanhXeDTO> searchBanhXe(String keyword) {
        return banhXeRepository.findByTenContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BanhXeDTO> getBanhXeByKichThuoc(String kichThuoc) {
        return banhXeRepository.findByKichThuoc(kichThuoc).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BanhXeDTO> getBanhXeByChatLieu(String chatLieu) {
        return banhXeRepository.findByChatLieu(chatLieu).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BanhXeDTO createBanhXe(BanhXeDTO banhXeDTO) {
        if (banhXeRepository.existsByTen(banhXeDTO.getTen())) {
            throw new RuntimeException("Bánh xe với tên này đã tồn tại");
        }
        
        BanhXe banhXe = mapToEntity(banhXeDTO);
        BanhXe savedBanhXe = banhXeRepository.save(banhXe);
        return mapToDTO(savedBanhXe);
    }

    public BanhXeDTO updateBanhXe(Long id, BanhXeDTO banhXeDTO) {
        BanhXe existingBanhXe = banhXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bánh xe với ID: " + id));
        
        updateEntityFromDTO(existingBanhXe, banhXeDTO);
        BanhXe updatedBanhXe = banhXeRepository.save(existingBanhXe);
        return mapToDTO(updatedBanhXe);
    }

    public void deleteBanhXe(Long id) {
        if (!banhXeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bánh xe với ID: " + id);
        }
        banhXeRepository.deleteById(id);
    }

    // Helper methods
    private BanhXeDTO mapToDTO(BanhXe banhXe) {
        BanhXeDTO dto = new BanhXeDTO();
        dto.setId(banhXe.getId());
        dto.setTen(banhXe.getTen());
        dto.setMoTa(banhXe.getMoTa());
        dto.setKichThuoc(banhXe.getKichThuoc());
        dto.setChatLieu(banhXe.getChatLieu());
        dto.setGiaThem(banhXe.getGiaThem());
        dto.setDuongDanAnh(banhXe.getDuongDanAnh());
        return dto;
    }

    private BanhXe mapToEntity(BanhXeDTO dto) {
        BanhXe banhXe = new BanhXe();
        updateEntityFromDTO(banhXe, dto);
        return banhXe;
    }

    private void updateEntityFromDTO(BanhXe banhXe, BanhXeDTO dto) {
        banhXe.setTen(dto.getTen());
        banhXe.setMoTa(dto.getMoTa());
        banhXe.setKichThuoc(dto.getKichThuoc());
        banhXe.setChatLieu(dto.getChatLieu());
        banhXe.setGiaThem(dto.getGiaThem());
        banhXe.setDuongDanAnh(dto.getDuongDanAnh());
    }
} 
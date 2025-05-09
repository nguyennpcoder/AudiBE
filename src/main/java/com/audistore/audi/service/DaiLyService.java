package com.audistore.audi.service;

import com.audistore.audi.dto.DaiLyDTO;
import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.TonKho;
import com.audistore.audi.repository.DaiLyRepository;
import com.audistore.audi.repository.TonKhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DaiLyService {

    private final DaiLyRepository daiLyRepository;
    private final TonKhoRepository tonKhoRepository;

    @Autowired
    public DaiLyService(DaiLyRepository daiLyRepository, TonKhoRepository tonKhoRepository) {
        this.daiLyRepository = daiLyRepository;
        this.tonKhoRepository = tonKhoRepository;
    }

    public List<DaiLyDTO> getAllDaiLy() {
        return daiLyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DaiLyDTO getDaiLyById(Long id) {
        return daiLyRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + id));
    }

    public List<DaiLyDTO> getDaiLyByThanhPho(String thanhPho) {
        return daiLyRepository.findByThanhPho(thanhPho).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DaiLyDTO> getDaiLyByTinh(String tinh) {
        return daiLyRepository.findByTinh(tinh).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DaiLyDTO> getDaiLyDichVu() {
        return daiLyRepository.findByLaTrungTamDichVu(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DaiLyDTO> searchDaiLy(String keyword) {
        return daiLyRepository.searchDaiLy(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DaiLyDTO> getDaiLyCoMauXe(Long mauXeId) {
        return daiLyRepository.findDaiLyCoMauXe(mauXeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DaiLyDTO createDaiLy(DaiLyDTO daiLyDTO) {
        DaiLy daiLy = mapToEntity(daiLyDTO);
        DaiLy savedDaiLy = daiLyRepository.save(daiLy);
        return mapToDTO(savedDaiLy);
    }

    public DaiLyDTO updateDaiLy(Long id, DaiLyDTO daiLyDTO) {
        DaiLy existingDaiLy = daiLyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + id));

        updateEntityFromDTO(existingDaiLy, daiLyDTO);
        DaiLy updatedDaiLy = daiLyRepository.save(existingDaiLy);
        return mapToDTO(updatedDaiLy);
    }

    public void deleteDaiLy(Long id) {
        if (!daiLyRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy đại lý với ID: " + id);
        }
        daiLyRepository.deleteById(id);
    }

    // Helper methods
    private DaiLyDTO mapToDTO(DaiLy daiLy) {
        DaiLyDTO dto = new DaiLyDTO();
        dto.setId(daiLy.getId());
        dto.setTen(daiLy.getTen());
        dto.setDiaChi(daiLy.getDiaChi());
        dto.setThanhPho(daiLy.getThanhPho());
        dto.setTinh(daiLy.getTinh());
        dto.setMaBuuDien(daiLy.getMaBuuDien());
        dto.setQuocGia(daiLy.getQuocGia());
        dto.setSoDienThoai(daiLy.getSoDienThoai());
        dto.setEmail(daiLy.getEmail());
        dto.setGioLamViec(daiLy.getGioLamViec());
        dto.setViTriDiaLy(daiLy.getViTriDiaLy());
        dto.setLaTrungTamDichVu(daiLy.getLaTrungTamDichVu());
        
        // Tính toán số lượng xe tồn kho
        long soLuongTonKho = daiLy.getDanhSachTonKho().stream()
                .filter(tonKho -> tonKho.getTrangThai() == TonKho.TrangThai.co_san)
                .count();
        dto.setSoLuongXeTonKho(soLuongTonKho);
        
        return dto;
    }

    private DaiLy mapToEntity(DaiLyDTO dto) {
        DaiLy daiLy = new DaiLy();
        updateEntityFromDTO(daiLy, dto);
        return daiLy;
    }

    private void updateEntityFromDTO(DaiLy daiLy, DaiLyDTO dto) {
        daiLy.setTen(dto.getTen());
        daiLy.setDiaChi(dto.getDiaChi());
        daiLy.setThanhPho(dto.getThanhPho());
        daiLy.setTinh(dto.getTinh());
        daiLy.setMaBuuDien(dto.getMaBuuDien());
        daiLy.setQuocGia(dto.getQuocGia());
        daiLy.setSoDienThoai(dto.getSoDienThoai());
        daiLy.setEmail(dto.getEmail());
        daiLy.setGioLamViec(dto.getGioLamViec());
        daiLy.setViTriDiaLy(dto.getViTriDiaLy());
        daiLy.setLaTrungTamDichVu(dto.getLaTrungTamDichVu());
    }
}
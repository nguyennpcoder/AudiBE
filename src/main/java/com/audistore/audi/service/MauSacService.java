package com.audistore.audi.service;

import com.audistore.audi.dto.MauSacDTO;
import com.audistore.audi.model.MauSac;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.repository.MauSacRepository;
import com.audistore.audi.repository.MauXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MauSacService {

    private final MauSacRepository mauSacRepository;
    private final MauXeRepository mauXeRepository;

    @Autowired
    public MauSacService(MauSacRepository mauSacRepository, MauXeRepository mauXeRepository) {
        this.mauSacRepository = mauSacRepository;
        this.mauXeRepository = mauXeRepository;
    }

    public List<MauSacDTO> getAllMauSac() {
        return mauSacRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MauSacDTO getMauSacById(Long id) {
        MauSac mauSac = mauSacRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + id));
        return convertToDTO(mauSac);
    }

    public List<MauSacDTO> getMauSacByMauXe(Long mauXeId) {
        MauXe mauXe = mauXeRepository.findById(mauXeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + mauXeId));

        return mauXe.getDanhSachMauSac().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MauSacDTO createMauSac(MauSacDTO mauSacDTO) {
        MauSac mauSac = convertToEntity(mauSacDTO);
        MauSac savedMauSac = mauSacRepository.save(mauSac);
        return convertToDTO(savedMauSac);
    }

    @Transactional
    public MauSacDTO updateMauSac(Long id, MauSacDTO mauSacDTO) {
        if (!mauSacRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy màu sắc với ID: " + id);
        }

        MauSac mauSac = convertToEntity(mauSacDTO);
        mauSac.setId(id);
        MauSac updatedMauSac = mauSacRepository.save(mauSac);
        return convertToDTO(updatedMauSac);
    }

    @Transactional
    public void deleteMauSac(Long id) {
        if (!mauSacRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy màu sắc với ID: " + id);
        }
        mauSacRepository.deleteById(id);
    }

    // Helper methods
    private MauSacDTO convertToDTO(MauSac mauSac) {
        MauSacDTO dto = new MauSacDTO();
        dto.setId(mauSac.getId());
        dto.setTen(mauSac.getTen());
        dto.setMaHex(mauSac.getMaHex());
        dto.setLaMetallic(mauSac.getLaMetallic());
        dto.setDuongDanAnh(mauSac.getDuongDanAnh());
        dto.setGiaThem(mauSac.getGiaThem());
        return dto;
    }

    private MauSac convertToEntity(MauSacDTO dto) {
        MauSac mauSac = new MauSac();
        if (dto.getId() != null) {
            mauSac.setId(dto.getId());
        }
        mauSac.setTen(dto.getTen());
        mauSac.setMaHex(dto.getMaHex());
        mauSac.setLaMetallic(dto.getLaMetallic() != null ? dto.getLaMetallic() : false);
        mauSac.setDuongDanAnh(dto.getDuongDanAnh());
        mauSac.setGiaThem(dto.getGiaThem() != null ? dto.getGiaThem() : BigDecimal.ZERO);
        return mauSac;
    }
}
// backend/audi/src/main/java/com/audistore/audi/service/HinhAnhXeTheoMauService.java
package com.audistore.audi.service;

import com.audistore.audi.dto.HinhAnhXeTheoMauDTO;
import com.audistore.audi.model.HinhAnhXeTheoMau;
import com.audistore.audi.repository.HinhAnhXeTheoMauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HinhAnhXeTheoMauService {

    @Autowired
    private HinhAnhXeTheoMauRepository hinhAnhXeTheoMauRepository;

    public List<HinhAnhXeTheoMauDTO> getHinhAnhByMauXe(Long idMauXe) {
        return hinhAnhXeTheoMauRepository.findByMauXeId(idMauXe).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<HinhAnhXeTheoMauDTO> getHinhAnhByMauXeAndMauSac(Long idMauXe, Long idMauSac) {
        return hinhAnhXeTheoMauRepository.findByMauXeIdAndMauSacId(idMauXe, idMauSac).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<HinhAnhXeTheoMauDTO> getHinhAnhByMauXeAndMauSacAndLoai(Long idMauXe, Long idMauSac, String loaiHinh) {
        return hinhAnhXeTheoMauRepository.findByMauXeIdAndMauSacIdAndLoaiHinh(idMauXe, idMauSac, loaiHinh).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private HinhAnhXeTheoMauDTO mapToDTO(HinhAnhXeTheoMau hinhAnhXeTheoMau) {
        HinhAnhXeTheoMauDTO dto = new HinhAnhXeTheoMauDTO();
        dto.setId(hinhAnhXeTheoMau.getId());
        dto.setIdMauXe(hinhAnhXeTheoMau.getMauXe().getId());
        dto.setIdMauSac(hinhAnhXeTheoMau.getMauSac().getId());
        dto.setTenMauSac(hinhAnhXeTheoMau.getMauSac().getTen());
        dto.setMaHex(hinhAnhXeTheoMau.getMauSac().getMaHex());
        dto.setDuongDanAnh(hinhAnhXeTheoMau.getDuongDanAnh());
        dto.setLoaiHinh(hinhAnhXeTheoMau.getLoaiHinh());
        dto.setViTri(hinhAnhXeTheoMau.getViTri());
        return dto;
    }
}
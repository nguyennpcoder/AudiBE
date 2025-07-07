package com.audistore.audi.service;

import com.audistore.audi.dto.HinhAnhTheoNoiThatDTO;
import com.audistore.audi.model.HinhAnhTheoNoiThat;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NoiThat;
import com.audistore.audi.repository.HinhAnhTheoNoiThatRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NoiThatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HinhAnhTheoNoiThatService {

    @Autowired
    private HinhAnhTheoNoiThatRepository hinhAnhTheoNoiThatRepository;

    @Autowired
    private MauXeRepository mauXeRepository;

    @Autowired
    private NoiThatRepository noiThatRepository;

    public List<HinhAnhTheoNoiThatDTO> getHinhAnhTheoNoiThat(Long mauXeId, Long noiThatId, Long mauSacId) {
        List<HinhAnhTheoNoiThat> hinhAnhList;
        if (mauSacId != null) {
            hinhAnhList = hinhAnhTheoNoiThatRepository.findByMauXeIdAndNoiThatIdAndMauSacIdOrderByViTri(mauXeId, noiThatId, mauSacId);
        } else {
            hinhAnhList = hinhAnhTheoNoiThatRepository.findByMauXeIdAndNoiThatIdOrderByViTri(mauXeId, noiThatId);
        }
        return hinhAnhList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<HinhAnhTheoNoiThatDTO> getHinhAnhTheoNoiThatVaLoai(Long mauXeId, Long noiThatId, String loaiHinh) {
        HinhAnhTheoNoiThat.LoaiHinh loai = HinhAnhTheoNoiThat.LoaiHinh.valueOf(loaiHinh);
        List<HinhAnhTheoNoiThat> hinhAnhList = hinhAnhTheoNoiThatRepository.findByMauXeIdAndNoiThatIdAndLoaiHinhOrderByViTri(mauXeId, noiThatId, loai);
        return hinhAnhList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public HinhAnhTheoNoiThatDTO createHinhAnhTheoNoiThat(HinhAnhTheoNoiThatDTO dto) {
        MauXe mauXe = mauXeRepository.findById(dto.getIdMau())
                .orElseThrow(() -> new RuntimeException("Mẫu xe không tồn tại"));

        NoiThat noiThat = noiThatRepository.findById(dto.getIdNoiThat())
                .orElseThrow(() -> new RuntimeException("Nội thất không tồn tại"));

        HinhAnhTheoNoiThat hinhAnh = new HinhAnhTheoNoiThat();
        hinhAnh.setMauXe(mauXe);
        hinhAnh.setNoiThat(noiThat);
        hinhAnh.setDuongDanAnh(dto.getDuongDanAnh());
        hinhAnh.setLoaiHinh(HinhAnhTheoNoiThat.LoaiHinh.valueOf(dto.getLoaiHinh()));
        hinhAnh.setViTri(dto.getViTri());

        HinhAnhTheoNoiThat saved = hinhAnhTheoNoiThatRepository.save(hinhAnh);
        return convertToDTO(saved);
    }

    private HinhAnhTheoNoiThatDTO convertToDTO(HinhAnhTheoNoiThat entity) {
        HinhAnhTheoNoiThatDTO dto = new HinhAnhTheoNoiThatDTO();
        dto.setId(entity.getId());
        dto.setIdMau(entity.getMauXe().getId());
        dto.setIdNoiThat(entity.getNoiThat().getId());
        dto.setTenNoiThat(entity.getNoiThat().getTen());
        dto.setDuongDanAnh(entity.getDuongDanAnh());
        dto.setLoaiHinh(entity.getLoaiHinh().name());
        dto.setViTri(entity.getViTri());
        dto.setIdMauSac(entity.getMauSac() != null ? entity.getMauSac().getId() : null);
        return dto;
    }
}
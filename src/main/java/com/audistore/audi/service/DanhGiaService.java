package com.audistore.audi.service;

import com.audistore.audi.dto.DanhGiaDTO;
import com.audistore.audi.model.DanhGia;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.DanhGiaRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DanhGiaService {

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private MauXeRepository mauXeRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DanhGiaDTO themDanhGia(DanhGiaDTO danhGiaDTO) {
        DanhGia danhGia = mapToEntity(danhGiaDTO);
        danhGia.setTrangThai(DanhGia.TrangThai.cho_duyet);
        DanhGia savedDanhGia = danhGiaRepository.save(danhGia);
        return mapToDTO(savedDanhGia);
    }

    public DanhGiaDTO getDanhGiaById(Long id) {
        DanhGia danhGia = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));
        return mapToDTO(danhGia);
    }

    public Page<DanhGiaDTO> getAllDanhGia(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<DanhGia> danhGias = danhGiaRepository.findAll(pageable);
        return danhGias.map(this::mapToDTO);
    }

    public Page<DanhGiaDTO> getDanhGiaByMauXe(Long idMauXe, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").descending());
        Page<DanhGia> danhGias = danhGiaRepository.findByMauXeIdAndTrangThai(
                idMauXe, DanhGia.TrangThai.da_duyet, pageable);
        return danhGias.map(this::mapToDTO);
    }

    public Page<DanhGiaDTO> getDanhGiaByNguoiDung(Long idNguoiDung, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").descending());
        Page<DanhGia> danhGias = danhGiaRepository.findByNguoiDungId(idNguoiDung, pageable);
        return danhGias.map(this::mapToDTO);
    }

    public Page<DanhGiaDTO> getDanhGiaChoDuyet(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").ascending());
        Page<DanhGia> danhGias = danhGiaRepository.findByTrangThai(DanhGia.TrangThai.cho_duyet, pageable);
        return danhGias.map(this::mapToDTO);
    }

    public DanhGiaDTO capNhatDanhGia(Long id, DanhGiaDTO danhGiaDTO) {
        DanhGia danhGia = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));

        danhGia.setTieuDe(danhGiaDTO.getTieuDe());
        danhGia.setNoiDung(danhGiaDTO.getNoiDung());
        danhGia.setSoSao(danhGiaDTO.getSoSao());
        
        if (danhGiaDTO.getDaMua() != null) {
            danhGia.setDaMua(danhGiaDTO.getDaMua());
        }

        DanhGia updatedDanhGia = danhGiaRepository.save(danhGia);
        return mapToDTO(updatedDanhGia);
    }

    public DanhGiaDTO duyetDanhGia(Long id, boolean approve) {
        DanhGia danhGia = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));

        danhGia.setTrangThai(approve ? DanhGia.TrangThai.da_duyet : DanhGia.TrangThai.bi_tu_choi);

        DanhGia updatedDanhGia = danhGiaRepository.save(danhGia);
        return mapToDTO(updatedDanhGia);
    }

    public void xoaDanhGia(Long id) {
        DanhGia danhGia = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));
        danhGiaRepository.delete(danhGia);
    }

    public double tinhTrungBinhSaoMauXe(Long idMauXe) {
        List<DanhGia> danhGias = danhGiaRepository.findByMauXeIdAndTrangThai(
                idMauXe, DanhGia.TrangThai.da_duyet);
        
        if (danhGias.isEmpty()) {
            return 0;
        }
        
        double tongSao = danhGias.stream()
                .mapToInt(DanhGia::getSoSao)
                .sum();
                
        return tongSao / danhGias.size();
    }

    private DanhGia mapToEntity(DanhGiaDTO dto) {
        DanhGia danhGia = new DanhGia();
        
        if (dto.getId() != null) {
            danhGia.setId(dto.getId());
        }
        
        // Set người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        danhGia.setNguoiDung(nguoiDung);
        
        // Set mẫu xe
        MauXe mauXe = mauXeRepository.findById(dto.getIdMauXe())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + dto.getIdMauXe()));
        danhGia.setMauXe(mauXe);
        
        danhGia.setSoSao(dto.getSoSao());
        danhGia.setTieuDe(dto.getTieuDe());
        danhGia.setNoiDung(dto.getNoiDung());
        
        if (dto.getDaMua() != null) {
            danhGia.setDaMua(dto.getDaMua());
        }
        
        if (dto.getTrangThai() != null) {
            danhGia.setTrangThai(DanhGia.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        return danhGia;
    }

    private DanhGiaDTO mapToDTO(DanhGia entity) {
        DanhGiaDTO dto = new DanhGiaDTO();
        
        dto.setId(entity.getId());
        dto.setIdNguoiDung(entity.getNguoiDung().getId());
        dto.setTenNguoiDung(entity.getNguoiDung().getHo() + " " + entity.getNguoiDung().getTen());
        dto.setIdMauXe(entity.getMauXe().getId());
        dto.setTenMauXe(entity.getMauXe().getTenMau());
        dto.setSoSao(entity.getSoSao());
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setDaMua(entity.getDaMua());
        
        if (entity.getNgayTao() != null) {
            dto.setNgayTao(entity.getNgayTao().format(formatter));
        }
        
        dto.setTrangThai(entity.getTrangThai().name());
        
        return dto;
    }
}
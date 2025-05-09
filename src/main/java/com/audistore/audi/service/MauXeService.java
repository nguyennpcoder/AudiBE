package com.audistore.audi.service;

import com.audistore.audi.dto.MauXeDTO;
import com.audistore.audi.model.DongXe;
import com.audistore.audi.model.MauSac;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.TuyChon;
import com.audistore.audi.repository.DongXeRepository;
import com.audistore.audi.repository.MauSacRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.TuyChonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MauXeService {

    private final MauXeRepository mauXeRepository;
    private final DongXeRepository dongXeRepository;
    private final MauSacRepository mauSacRepository;
    private final TuyChonRepository tuyChonRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public MauXeService(MauXeRepository mauXeRepository, DongXeRepository dongXeRepository,
                        MauSacRepository mauSacRepository, TuyChonRepository tuyChonRepository) {
        this.mauXeRepository = mauXeRepository;
        this.dongXeRepository = dongXeRepository;
        this.mauSacRepository = mauSacRepository;
        this.tuyChonRepository = tuyChonRepository;
    }

    public List<MauXeDTO> getAllMauXe() {
        return mauXeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MauXeDTO getMauXeById(Long id) {
        return mauXeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + id));
    }

    public List<MauXeDTO> getMauXeByDongXe(Long dongXeId) {
        DongXe dongXe = dongXeRepository.findById(dongXeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng xe với ID: " + dongXeId));

        return mauXeRepository.findByDongXe(dongXe).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MauXeDTO> getMauXeByNamSanXuat(Integer namSanXuat) {
        return mauXeRepository.findByNamSanXuat(namSanXuat).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MauXeDTO> getMauXeByConHang(Boolean conHang) {
        return mauXeRepository.findByConHang(conHang).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MauXeDTO> searchMauXe(String keyword) {
        return mauXeRepository.findByTenMauContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MauXeDTO> getMauXeByGiaTrongKhoang(BigDecimal giaTu, BigDecimal giaDen) {
        return mauXeRepository.findByGiaTrongKhoang(giaTu, giaDen).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MauXeDTO> getMauXeByPhanLoai(String phanLoai) {
        try {
            DongXe.PhanLoai loai = DongXe.PhanLoai.valueOf(phanLoai);
            return mauXeRepository.findByPhanLoai(loai).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Phân loại không hợp lệ: " + phanLoai);
        }
    }

    public MauXeDTO createMauXe(MauXeDTO mauXeDTO) {
        MauXe mauXe = mapToEntity(mauXeDTO);
        MauXe savedMauXe = mauXeRepository.save(mauXe);
        return mapToDTO(savedMauXe);
    }

    public MauXeDTO updateMauXe(Long id, MauXeDTO mauXeDTO) {
        MauXe existingMauXe = mauXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + id));

        updateEntityFromDTO(existingMauXe, mauXeDTO);
        MauXe updatedMauXe = mauXeRepository.save(existingMauXe);
        return mapToDTO(updatedMauXe);
    }

    public void deleteMauXe(Long id) {
        if (!mauXeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy mẫu xe với ID: " + id);
        }
        mauXeRepository.deleteById(id);
    }

    // Helper methods
    private MauXeDTO mapToDTO(MauXe mauXe) {
        MauXeDTO dto = new MauXeDTO();
        dto.setId(mauXe.getId());
        dto.setIdDong(mauXe.getDongXe().getId());
        dto.setTenDong(mauXe.getDongXe().getTen());
        dto.setTenMau(mauXe.getTenMau());
        dto.setNamSanXuat(mauXe.getNamSanXuat());
        dto.setGiaCoban(mauXe.getGiaCoban());
        dto.setMoTa(mauXe.getMoTa());
        dto.setThongSoKyThuat(mauXe.getThongSoKyThuat());
        dto.setConHang(mauXe.getConHang());

        // Chuyển LocalDate thành String
        if (mauXe.getNgayRaMat() != null) {
            dto.setNgayRaMat(mauXe.getNgayRaMat().format(DATE_FORMATTER));
        }

        // Lấy danh sách ID màu sắc
        List<Long> idsMauSac = mauXe.getDanhSachMauSac().stream()
                .map(MauSac::getId)
                .collect(Collectors.toList());
        dto.setIdsMauSac(idsMauSac);

        // Lấy danh sách ID tùy chọn
        List<Long> idsTuyChon = mauXe.getDanhSachTuyChon().stream()
                .map(TuyChon::getId)
                .collect(Collectors.toList());
        dto.setIdsTuyChon(idsTuyChon);

        return dto;
    }

    private MauXe mapToEntity(MauXeDTO dto) {
        MauXe mauXe = new MauXe();
        updateEntityFromDTO(mauXe, dto);
        return mauXe;
    }

    private void updateEntityFromDTO(MauXe mauXe, MauXeDTO dto) {
        DongXe dongXe = dongXeRepository.findById(dto.getIdDong())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng xe với ID: " + dto.getIdDong()));

        mauXe.setDongXe(dongXe);
        mauXe.setTenMau(dto.getTenMau());
        mauXe.setNamSanXuat(dto.getNamSanXuat());
        mauXe.setGiaCoban(dto.getGiaCoban());
        mauXe.setMoTa(dto.getMoTa());
        mauXe.setThongSoKyThuat(dto.getThongSoKyThuat());
        mauXe.setConHang(dto.getConHang());

        // Chuyển String thành LocalDate
        if (dto.getNgayRaMat() != null && !dto.getNgayRaMat().isEmpty()) {
            try {
                LocalDate ngayRaMat = LocalDate.parse(dto.getNgayRaMat(), DATE_FORMATTER);
                mauXe.setNgayRaMat(ngayRaMat);
            } catch (Exception e) {
                throw new RuntimeException("Định dạng ngày không hợp lệ. Sử dụng định dạng yyyy-MM-dd");
            }
        }

        // Cập nhật danh sách màu sắc
        if (dto.getIdsMauSac() != null && !dto.getIdsMauSac().isEmpty()) {
            List<MauSac> mauSacList = new ArrayList<>();
            for (Long mauSacId : dto.getIdsMauSac()) {
                MauSac mauSac = mauSacRepository.findById(mauSacId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + mauSacId));
                mauSacList.add(mauSac);
            }
            mauXe.setDanhSachMauSac(mauSacList);
        }

        // Cập nhật danh sách tùy chọn
        if (dto.getIdsTuyChon() != null && !dto.getIdsTuyChon().isEmpty()) {
            List<TuyChon> tuyChonList = new ArrayList<>();
            for (Long tuyChonId : dto.getIdsTuyChon()) {
                TuyChon tuyChon = tuyChonRepository.findById(tuyChonId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tùy chọn với ID: " + tuyChonId));
                tuyChonList.add(tuyChon);
            }
            mauXe.setDanhSachTuyChon(tuyChonList);
        }
    }
}
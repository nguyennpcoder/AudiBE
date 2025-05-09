package com.audistore.audi.service;

import com.audistore.audi.dto.KiemTraTonKhoDTO;
import com.audistore.audi.dto.TonKhoDTO;
import com.audistore.audi.model.*;
import com.audistore.audi.repository.DaiLyRepository;
import com.audistore.audi.repository.MauSacRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.TonKhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TonKhoService {

    private final TonKhoRepository tonKhoRepository;
    private final MauXeRepository mauXeRepository;
    private final DaiLyRepository daiLyRepository;
    private final MauSacRepository mauSacRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public TonKhoService(TonKhoRepository tonKhoRepository, MauXeRepository mauXeRepository,
                          DaiLyRepository daiLyRepository, MauSacRepository mauSacRepository) {
        this.tonKhoRepository = tonKhoRepository;
        this.mauXeRepository = mauXeRepository;
        this.daiLyRepository = daiLyRepository;
        this.mauSacRepository = mauSacRepository;
    }

    public List<TonKhoDTO> getAllTonKho() {
        return tonKhoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TonKhoDTO getTonKhoById(Long id) {
        return tonKhoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + id));
    }

    public TonKhoDTO getTonKhoBySoKhung(String soKhung) {
        return tonKhoRepository.findBySoKhung(soKhung)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với số khung: " + soKhung));
    }

    public List<TonKhoDTO> getTonKhoByMauXe(Long mauXeId) {
        MauXe mauXe = mauXeRepository.findById(mauXeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + mauXeId));
        
        return tonKhoRepository.findByMauXe(mauXe).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TonKhoDTO> getTonKhoByDaiLy(Long daiLyId) {
        DaiLy daiLy = daiLyRepository.findById(daiLyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + daiLyId));
        
        return tonKhoRepository.findByDaiLy(daiLy).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TonKhoDTO> getTonKhoByTrangThai(String trangThai) {
        try {
            TonKho.TrangThai trangThaiEnum = TonKho.TrangThai.valueOf(trangThai);
            return tonKhoRepository.findByTrangThai(trangThaiEnum).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + trangThai);
        }
    }

    public List<TonKhoDTO> getXeCoSanTaiDaiLy(Long mauXeId, Long daiLyId) {
        return tonKhoRepository.findAvailableByMauXeAndDaiLy(mauXeId, daiLyId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public KiemTraTonKhoDTO kiemTraTonKho(Long mauXeId) {
        MauXe mauXe = mauXeRepository.findById(mauXeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + mauXeId));
        
        List<TonKho> danhSachCoSan = tonKhoRepository.findAvailableByMauXe(mauXeId);
        
        // Nhóm tồn kho theo đại lý
        Map<DaiLy, List<TonKho>> tonKhoTheoDaiLy = danhSachCoSan.stream()
                .collect(Collectors.groupingBy(TonKho::getDaiLy));
        
        // Tạo kết quả trả về
        KiemTraTonKhoDTO result = new KiemTraTonKhoDTO();
        result.setIdMau(mauXe.getId());
        result.setTenMau(mauXe.getTenMau());
        result.setTongSoLuong(tonKhoRepository.countAvailableByMauXe(mauXeId));
        result.setSoLuongCoSan(danhSachCoSan.size());
        
        // Thêm chi tiết theo đại lý
        List<KiemTraTonKhoDTO.TonKhoTheoDaiLyDTO> chiTiet = new ArrayList<>();
        tonKhoTheoDaiLy.forEach((daiLy, danhSach) -> {
            KiemTraTonKhoDTO.TonKhoTheoDaiLyDTO daiLyDTO = new KiemTraTonKhoDTO.TonKhoTheoDaiLyDTO();
            daiLyDTO.setIdDaiLy(daiLy.getId());
            daiLyDTO.setTenDaiLy(daiLy.getTen());
            daiLyDTO.setThanhPho(daiLy.getThanhPho());
            daiLyDTO.setSoLuongCoSan(danhSach.size());
            daiLyDTO.setSoDienThoai(daiLy.getSoDienThoai());
            chiTiet.add(daiLyDTO);
        });
        
        result.setChiTietTheoDaiLy(chiTiet);
        return result;
    }

    public TonKhoDTO createTonKho(TonKhoDTO tonKhoDTO) {
        if (tonKhoDTO.getSoKhung() != null && !tonKhoDTO.getSoKhung().isEmpty() &&
                tonKhoRepository.findBySoKhung(tonKhoDTO.getSoKhung()).isPresent()) {
            throw new RuntimeException("Số khung đã tồn tại trong hệ thống: " + tonKhoDTO.getSoKhung());
        }
        
        TonKho tonKho = mapToEntity(tonKhoDTO);
        TonKho savedTonKho = tonKhoRepository.save(tonKho);
        return mapToDTO(savedTonKho);
    }

    public TonKhoDTO updateTonKho(Long id, TonKhoDTO tonKhoDTO) {
        TonKho existingTonKho = tonKhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + id));
        
        // Kiểm tra nếu số khung được cập nhật và đã tồn tại
        if (tonKhoDTO.getSoKhung() != null && !tonKhoDTO.getSoKhung().isEmpty() &&
                !tonKhoDTO.getSoKhung().equals(existingTonKho.getSoKhung()) &&
                tonKhoRepository.findBySoKhung(tonKhoDTO.getSoKhung()).isPresent()) {
            throw new RuntimeException("Số khung đã tồn tại trong hệ thống: " + tonKhoDTO.getSoKhung());
        }
        
        updateEntityFromDTO(existingTonKho, tonKhoDTO);
        TonKho updatedTonKho = tonKhoRepository.save(existingTonKho);
        return mapToDTO(updatedTonKho);
    }

    public void deleteTonKho(Long id) {
        if (!tonKhoRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tồn kho với ID: " + id);
        }
        tonKhoRepository.deleteById(id);
    }

    public TonKhoDTO capNhatTrangThaiTonKho(Long id, String trangThai) {
        TonKho tonKho = tonKhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + id));
        
        try {
            TonKho.TrangThai trangThaiEnum = TonKho.TrangThai.valueOf(trangThai);
            tonKho.setTrangThai(trangThaiEnum);
            TonKho updatedTonKho = tonKhoRepository.save(tonKho);
            return mapToDTO(updatedTonKho);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + trangThai);
        }
    }

    // Helper methods
    private TonKhoDTO mapToDTO(TonKho tonKho) {
        TonKhoDTO dto = new TonKhoDTO();
        dto.setId(tonKho.getId());
        dto.setIdMau(tonKho.getMauXe().getId());
        dto.setTenMau(tonKho.getMauXe().getTenMau());
        dto.setIdDaiLy(tonKho.getDaiLy().getId());
        dto.setTenDaiLy(tonKho.getDaiLy().getTen());
        dto.setIdMauSac(tonKho.getMauSac().getId());
        dto.setTenMauSac(tonKho.getMauSac().getTen());
        dto.setSoKhung(tonKho.getSoKhung());
        dto.setTrangThai(tonKho.getTrangThai().name());
        
        if (tonKho.getNgaySanXuat() != null) {
            dto.setNgaySanXuat(tonKho.getNgaySanXuat().format(DATE_FORMATTER));
        }
        
        if (tonKho.getNgayVeDaiLy() != null) {
            dto.setNgayVeDaiLy(tonKho.getNgayVeDaiLy().format(DATE_FORMATTER));
        }
        
        dto.setTinhNangThem(tonKho.getTinhNangThem());
        dto.setGiaCuoiCung(tonKho.getGiaCuoiCung());
        
        return dto;
    }

    private TonKho mapToEntity(TonKhoDTO dto) {
        TonKho tonKho = new TonKho();
        updateEntityFromDTO(tonKho, dto);
        return tonKho;
    }

    private void updateEntityFromDTO(TonKho tonKho, TonKhoDTO dto) {
        MauXe mauXe = mauXeRepository.findById(dto.getIdMau())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + dto.getIdMau()));
        
        DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
        
        MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + dto.getIdMauSac()));
        
        tonKho.setMauXe(mauXe);
        tonKho.setDaiLy(daiLy);
        tonKho.setMauSac(mauSac);
        tonKho.setSoKhung(dto.getSoKhung());
        
        if (dto.getTrangThai() != null && !dto.getTrangThai().isEmpty()) {
            try {
                TonKho.TrangThai trangThai = TonKho.TrangThai.valueOf(dto.getTrangThai());
                tonKho.setTrangThai(trangThai);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Trạng thái không hợp lệ: " + dto.getTrangThai());
            }
        }
        
        if (dto.getNgaySanXuat() != null && !dto.getNgaySanXuat().isEmpty()) {
            try {
                LocalDate ngaySanXuat = LocalDate.parse(dto.getNgaySanXuat(), DATE_FORMATTER);
                tonKho.setNgaySanXuat(ngaySanXuat);
            } catch (Exception e) {
                throw new RuntimeException("Định dạng ngày sản xuất không hợp lệ. Sử dụng định dạng yyyy-MM-dd");
            }
        }
        
        if (dto.getNgayVeDaiLy() != null && !dto.getNgayVeDaiLy().isEmpty()) {
            try {
                LocalDate ngayVeDaiLy = LocalDate.parse(dto.getNgayVeDaiLy(), DATE_FORMATTER);
                tonKho.setNgayVeDaiLy(ngayVeDaiLy);
            } catch (Exception e) {
                throw new RuntimeException("Định dạng ngày về đại lý không hợp lệ. Sử dụng định dạng yyyy-MM-dd");
            }
        }
        
        tonKho.setTinhNangThem(dto.getTinhNangThem());
        tonKho.setGiaCuoiCung(dto.getGiaCuoiCung());
    }
}
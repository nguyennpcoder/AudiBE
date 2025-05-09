package com.audistore.audi.service;

import com.audistore.audi.dto.LaiThuDTO;
import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.LaiThu;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.DaiLyRepository;
import com.audistore.audi.repository.LaiThuRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LaiThuService {

    private final LaiThuRepository laiThuRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final MauXeRepository mauXeRepository;
    private final DaiLyRepository daiLyRepository;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public LaiThuService(
            LaiThuRepository laiThuRepository,
            NguoiDungRepository nguoiDungRepository,
            MauXeRepository mauXeRepository,
            DaiLyRepository daiLyRepository) {
        this.laiThuRepository = laiThuRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.mauXeRepository = mauXeRepository;
        this.daiLyRepository = daiLyRepository;
    }

    public List<LaiThuDTO> getAllLaiThu() {
        return laiThuRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LaiThuDTO getLaiThuById(Long id) {
        return laiThuRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch lái thử với ID: " + id));
    }
    
    public List<LaiThuDTO> getLaiThuByNguoiDung(Long idNguoiDung) {
        return laiThuRepository.findByIdNguoiDungOrderByThoiGianHenDesc(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LaiThuDTO> getLaiThuByDaiLy(Long idDaiLy) {
        return laiThuRepository.findByIdDaiLyOrderByThoiGianHenAsc(idDaiLy).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LaiThuDTO> getLaiThuByTrangThai(String trangThai) {
        LaiThu.TrangThai trangThaiEnum = LaiThu.TrangThai.valueOf(trangThai);
        return laiThuRepository.findByTrangThai(trangThaiEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LaiThuDTO> getLaiThuByNgay(Long idDaiLy, LocalDateTime ngay) {
        return laiThuRepository.findByDaiLyAndNgay(idDaiLy, ngay).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public LaiThuDTO createLaiThu(LaiThuDTO laiThuDTO) {
        // Kiểm tra xem đại lý có lịch trống không
        List<LaiThu> lichTrongNgay = laiThuRepository.findByDaiLyAndNgay(
                laiThuDTO.getIdDaiLy(), laiThuDTO.getThoiGianHen());
        
        // Kiểm tra số lượng lịch lái thử trong cùng khung giờ
        int soLichTrungGio = 0;
        LocalDateTime gioBatDau = laiThuDTO.getThoiGianHen().minusMinutes(30);
        LocalDateTime gioKetThuc = laiThuDTO.getThoiGianHen().plusMinutes(30);
        
        for (LaiThu laiThu : lichTrongNgay) {
            if (laiThu.getThoiGianHen().isAfter(gioBatDau) && laiThu.getThoiGianHen().isBefore(gioKetThuc)) {
                soLichTrungGio++;
            }
        }
        
        // Giới hạn 2 lịch lái thử cùng khung giờ
        if (soLichTrungGio >= 2) {
            throw new RuntimeException("Đại lý đã hết slot lái thử trong khung giờ này, vui lòng chọn thời gian khác");
        }
        
        LaiThu laiThu = mapToEntity(laiThuDTO);
        return mapToDTO(laiThuRepository.save(laiThu));
    }
    
    public LaiThuDTO updateLaiThu(Long id, LaiThuDTO laiThuDTO) {
        LaiThu existingLaiThu = laiThuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch lái thử với ID: " + id));
        
        // Nếu đã xác nhận hoặc hoàn thành thì không được chỉnh sửa thời gian lái thử
        if ((existingLaiThu.getTrangThai() == LaiThu.TrangThai.da_xac_nhan || 
                existingLaiThu.getTrangThai() == LaiThu.TrangThai.hoan_thanh) && 
                !existingLaiThu.getThoiGianHen().equals(laiThuDTO.getThoiGianHen())) {
            throw new RuntimeException("Không thể thay đổi thời gian cho lịch lái thử đã được xác nhận");
        }
        
        // Cập nhật thông tin lái thử
        updateEntityFromDTO(existingLaiThu, laiThuDTO);
        
        return mapToDTO(laiThuRepository.save(existingLaiThu));
    }
    
    public LaiThuDTO updateTrangThaiLaiThu(Long id, String trangThai) {
        LaiThu existingLaiThu = laiThuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch lái thử với ID: " + id));
        
        existingLaiThu.setTrangThai(LaiThu.TrangThai.valueOf(trangThai));
        
        return mapToDTO(laiThuRepository.save(existingLaiThu));
    }
    
    public void deleteLaiThu(Long id) {
        LaiThu laiThu = laiThuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch lái thử với ID: " + id));
        
        // Chỉ xóa được khi ở trạng thái chờ duyệt hoặc đã hủy
        if (laiThu.getTrangThai() == LaiThu.TrangThai.da_xac_nhan || 
                laiThu.getTrangThai() == LaiThu.TrangThai.hoan_thanh) {
            throw new RuntimeException("Không thể xóa lịch lái thử đã được xác nhận hoặc hoàn thành");
        }
        
        laiThuRepository.deleteById(id);
    }
    
    private LaiThu mapToEntity(LaiThuDTO dto) {
        LaiThu laiThu = new LaiThu();
        
        if (dto.getId() != null) {
            laiThu.setId(dto.getId());
        }
        
        // Set NguoiDung
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        laiThu.setNguoiDung(nguoiDung);
        
        // Set MauXe
        MauXe mauXe = mauXeRepository.findById(dto.getIdMau())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + dto.getIdMau()));
        laiThu.setMauXe(mauXe);
        
        // Set DaiLy
        DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
        laiThu.setDaiLy(daiLy);
        
        // Set các thông tin khác
        laiThu.setThoiGianHen(dto.getThoiGianHen());
        
        if (dto.getTrangThai() != null) {
            laiThu.setTrangThai(LaiThu.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        laiThu.setGhiChu(dto.getGhiChu());
        
        return laiThu;
    }
    
    private void updateEntityFromDTO(LaiThu laiThu, LaiThuDTO dto) {
        // Không đổi người dùng
        
        // Cập nhật MauXe nếu có thay đổi
        if (dto.getIdMau() != null && !dto.getIdMau().equals(laiThu.getMauXe().getId())) {
            MauXe mauXe = mauXeRepository.findById(dto.getIdMau())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + dto.getIdMau()));
            laiThu.setMauXe(mauXe);
        }
        
        // Cập nhật DaiLy nếu có thay đổi
        if (dto.getIdDaiLy() != null && !dto.getIdDaiLy().equals(laiThu.getDaiLy().getId())) {
            DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
            laiThu.setDaiLy(daiLy);
        }
        
        // Cập nhật các thông tin khác
        if (dto.getThoiGianHen() != null) {
            laiThu.setThoiGianHen(dto.getThoiGianHen());
        }
        
        if (dto.getTrangThai() != null) {
            laiThu.setTrangThai(LaiThu.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        if (dto.getGhiChu() != null) {
            laiThu.setGhiChu(dto.getGhiChu());
        }
    }
    
    private LaiThuDTO mapToDTO(LaiThu laiThu) {
        LaiThuDTO dto = new LaiThuDTO();
        
        dto.setId(laiThu.getId());
        
        // NguoiDung info
        if (laiThu.getNguoiDung() != null) {
            dto.setIdNguoiDung(laiThu.getNguoiDung().getId());
            dto.setTenNguoiDung(laiThu.getNguoiDung().getHo() + " " + laiThu.getNguoiDung().getTen());
        }
        
        // MauXe info
        if (laiThu.getMauXe() != null) {
            dto.setIdMau(laiThu.getMauXe().getId());
            dto.setTenMau(laiThu.getMauXe().getTenMau());
        }
        
        // DaiLy info
        if (laiThu.getDaiLy() != null) {
            dto.setIdDaiLy(laiThu.getDaiLy().getId());
            dto.setTenDaiLy(laiThu.getDaiLy().getTen());
        }
        
        // Các thông tin khác
        dto.setThoiGianHen(laiThu.getThoiGianHen());
        
        if (laiThu.getTrangThai() != null) {
            dto.setTrangThai(laiThu.getTrangThai().name());
        }
        
        dto.setGhiChu(laiThu.getGhiChu());
        
        if (laiThu.getNgayTao() != null) {
            dto.setNgayTao(laiThu.getNgayTao().format(DATETIME_FORMATTER));
        }
        
        return dto;
    }
}
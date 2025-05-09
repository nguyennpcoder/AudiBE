package com.audistore.audi.service;

import com.audistore.audi.dto.LichBaoDuongDTO;
import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.LichBaoDuong;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.DaiLyRepository;
import com.audistore.audi.repository.LichBaoDuongRepository;
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
public class LichBaoDuongService {

    private final LichBaoDuongRepository lichBaoDuongRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DaiLyRepository daiLyRepository;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public LichBaoDuongService(
            LichBaoDuongRepository lichBaoDuongRepository,
            NguoiDungRepository nguoiDungRepository,
            DaiLyRepository daiLyRepository) {
        this.lichBaoDuongRepository = lichBaoDuongRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.daiLyRepository = daiLyRepository;
    }

    public List<LichBaoDuongDTO> getAllLichBaoDuong() {
        return lichBaoDuongRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LichBaoDuongDTO getLichBaoDuongById(Long id) {
        return lichBaoDuongRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + id));
    }
    
    public List<LichBaoDuongDTO> getLichBaoDuongByNguoiDung(Long idNguoiDung) {
        return lichBaoDuongRepository.findByIdNguoiDungOrderByNgayHenDesc(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LichBaoDuongDTO> getLichBaoDuongByDaiLy(Long idDaiLy) {
        return lichBaoDuongRepository.findByIdDaiLyOrderByNgayHenAsc(idDaiLy).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LichBaoDuongDTO> getLichBaoDuongByTrangThai(String trangThai) {
        LichBaoDuong.TrangThai trangThaiEnum = LichBaoDuong.TrangThai.valueOf(trangThai);
        return lichBaoDuongRepository.findByTrangThai(trangThaiEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LichBaoDuongDTO> getLichBaoDuongByLoaiDichVu(String loaiDichVu) {
        LichBaoDuong.LoaiDichVu loaiDichVuEnum = LichBaoDuong.LoaiDichVu.valueOf(loaiDichVu);
        return lichBaoDuongRepository.findByLoaiDichVu(loaiDichVuEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LichBaoDuongDTO> getLichBaoDuongBySoKhung(String soKhung) {
        return lichBaoDuongRepository.findBySoKhungContainingIgnoreCase(soKhung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<LichBaoDuongDTO> getLichBaoDuongByNgay(Long idDaiLy, LocalDateTime ngay) {
        return lichBaoDuongRepository.findByDaiLyAndNgay(idDaiLy, ngay).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public LichBaoDuongDTO createLichBaoDuong(LichBaoDuongDTO lichBaoDuongDTO) {
        // Kiểm tra xem đại lý có lịch trống không
        List<LichBaoDuong> lichTrongNgay = lichBaoDuongRepository.findByDaiLyAndNgay(
                lichBaoDuongDTO.getIdDaiLy(), lichBaoDuongDTO.getNgayHen());
        
        // Kiểm tra số lượng lịch bảo dưỡng trong cùng khung giờ
        int soLichTrungGio = 0;
        LocalDateTime gioBatDau = lichBaoDuongDTO.getNgayHen().minusMinutes(45);
        LocalDateTime gioKetThuc = lichBaoDuongDTO.getNgayHen().plusMinutes(45);
        
        for (LichBaoDuong lich : lichTrongNgay) {
            if (lich.getNgayHen().isAfter(gioBatDau) && lich.getNgayHen().isBefore(gioKetThuc)) {
                soLichTrungGio++;
            }
        }
        
        // Giới hạn số lịch bảo dưỡng cùng khung giờ tùy theo đại lý
        DaiLy daiLy = daiLyRepository.findById(lichBaoDuongDTO.getIdDaiLy())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + lichBaoDuongDTO.getIdDaiLy()));
        
        int maxAppointments = daiLy.getLaTrungTamDichVu() ? 3 : 1;
        
        if (soLichTrungGio >= maxAppointments) {
            throw new RuntimeException("Đại lý đã hết slot bảo dưỡng trong khung giờ này, vui lòng chọn thời gian khác");
        }
        
        // Kiểm tra số khung VIN hợp lệ
        if (!lichBaoDuongDTO.getSoKhung().matches("^[A-HJ-NPR-Z0-9]{17}$")) {
            throw new RuntimeException("Số khung VIN không hợp lệ. Phải có 17 ký tự và không chứa I, O, Q");
        }
        
        LichBaoDuong lichBaoDuong = mapToEntity(lichBaoDuongDTO);
        return mapToDTO(lichBaoDuongRepository.save(lichBaoDuong));
    }
    
    public LichBaoDuongDTO updateLichBaoDuong(Long id, LichBaoDuongDTO lichBaoDuongDTO) {
        LichBaoDuong existingLichBaoDuong = lichBaoDuongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + id));
        
        // Không cho phép thay đổi nếu đang thực hiện hoặc đã hoàn thành
        if ((existingLichBaoDuong.getTrangThai() == LichBaoDuong.TrangThai.dang_thuc_hien || 
                existingLichBaoDuong.getTrangThai() == LichBaoDuong.TrangThai.hoan_thanh) && 
                !existingLichBaoDuong.getNgayHen().equals(lichBaoDuongDTO.getNgayHen())) {
            throw new RuntimeException("Không thể thay đổi thời gian cho lịch bảo dưỡng đang thực hiện hoặc đã hoàn thành");
        }
        
        // Cập nhật thông tin
        updateEntityFromDTO(existingLichBaoDuong, lichBaoDuongDTO);
        
        return mapToDTO(lichBaoDuongRepository.save(existingLichBaoDuong));
    }
    
    public LichBaoDuongDTO updateTrangThaiLichBaoDuong(Long id, String trangThai) {
        LichBaoDuong existingLichBaoDuong = lichBaoDuongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + id));
        
        existingLichBaoDuong.setTrangThai(LichBaoDuong.TrangThai.valueOf(trangThai));
        
        return mapToDTO(lichBaoDuongRepository.save(existingLichBaoDuong));
    }
    
    public void deleteLichBaoDuong(Long id) {
        LichBaoDuong lichBaoDuong = lichBaoDuongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + id));
        
        // Chỉ xóa được khi ở trạng thái đã đặt lịch hoặc đã hủy
        if (lichBaoDuong.getTrangThai() == LichBaoDuong.TrangThai.dang_thuc_hien || 
                lichBaoDuong.getTrangThai() == LichBaoDuong.TrangThai.hoan_thanh) {
            throw new RuntimeException("Không thể xóa lịch bảo dưỡng đang thực hiện hoặc đã hoàn thành");
        }
        
        lichBaoDuongRepository.deleteById(id);
    }
    
    private LichBaoDuong mapToEntity(LichBaoDuongDTO dto) {
        LichBaoDuong lichBaoDuong = new LichBaoDuong();
        
        if (dto.getId() != null) {
            lichBaoDuong.setId(dto.getId());
        }
        
        // Set NguoiDung
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        lichBaoDuong.setNguoiDung(nguoiDung);
        
        // Set DaiLy
        DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
        lichBaoDuong.setDaiLy(daiLy);
        
        // Set các thông tin khác
        lichBaoDuong.setSoKhung(dto.getSoKhung());
        lichBaoDuong.setNgayHen(dto.getNgayHen());
        
        if (dto.getLoaiDichVu() != null) {
            lichBaoDuong.setLoaiDichVu(LichBaoDuong.LoaiDichVu.valueOf(dto.getLoaiDichVu()));
        }
        
        lichBaoDuong.setMoTa(dto.getMoTa());
        
        if (dto.getTrangThai() != null) {
            lichBaoDuong.setTrangThai(LichBaoDuong.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        return lichBaoDuong;
    }
    
    private void updateEntityFromDTO(LichBaoDuong lichBaoDuong, LichBaoDuongDTO dto) {
        // Không đổi người dùng
        
        // Cập nhật DaiLy nếu có thay đổi
        if (dto.getIdDaiLy() != null && !dto.getIdDaiLy().equals(lichBaoDuong.getDaiLy().getId())) {
            DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
            lichBaoDuong.setDaiLy(daiLy);
        }
        
        // Cập nhật các thông tin khác
        if (dto.getSoKhung() != null) {
            lichBaoDuong.setSoKhung(dto.getSoKhung());
        }
        
        if (dto.getNgayHen() != null) {
            lichBaoDuong.setNgayHen(dto.getNgayHen());
        }
        
        if (dto.getLoaiDichVu() != null) {
            lichBaoDuong.setLoaiDichVu(LichBaoDuong.LoaiDichVu.valueOf(dto.getLoaiDichVu()));
        }
        
        if (dto.getMoTa() != null) {
            lichBaoDuong.setMoTa(dto.getMoTa());
        }
        
        if (dto.getTrangThai() != null) {
            lichBaoDuong.setTrangThai(LichBaoDuong.TrangThai.valueOf(dto.getTrangThai()));
        }
    }
    
    private LichBaoDuongDTO mapToDTO(LichBaoDuong lichBaoDuong) {
        LichBaoDuongDTO dto = new LichBaoDuongDTO();
        
        dto.setId(lichBaoDuong.getId());
        
        // NguoiDung info
        if (lichBaoDuong.getNguoiDung() != null) {
            dto.setIdNguoiDung(lichBaoDuong.getNguoiDung().getId());
            dto.setTenNguoiDung(lichBaoDuong.getNguoiDung().getHo() + " " + lichBaoDuong.getNguoiDung().getTen());
        }
        
        // DaiLy info
        if (lichBaoDuong.getDaiLy() != null) {
            dto.setIdDaiLy(lichBaoDuong.getDaiLy().getId());
            dto.setTenDaiLy(lichBaoDuong.getDaiLy().getTen());
        }
        
        // Các thông tin khác
        dto.setSoKhung(lichBaoDuong.getSoKhung());
        dto.setNgayHen(lichBaoDuong.getNgayHen());
        
        if (lichBaoDuong.getLoaiDichVu() != null) {
            dto.setLoaiDichVu(lichBaoDuong.getLoaiDichVu().name());
        }
        
        dto.setMoTa(lichBaoDuong.getMoTa());
        
        if (lichBaoDuong.getTrangThai() != null) {
            dto.setTrangThai(lichBaoDuong.getTrangThai().name());
        }
        
        if (lichBaoDuong.getNgayTao() != null) {
            dto.setNgayTao(lichBaoDuong.getNgayTao().format(DATETIME_FORMATTER));
        }
        
        return dto;
    }
}
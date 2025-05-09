package com.audistore.audi.service;

import com.audistore.audi.dto.ThanhToanDTO;
import com.audistore.audi.model.DonHang;
import com.audistore.audi.model.ThanhToan;
import com.audistore.audi.repository.DonHangRepository;
import com.audistore.audi.repository.ThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public ThanhToanService(ThanhToanRepository thanhToanRepository, DonHangRepository donHangRepository) {
        this.thanhToanRepository = thanhToanRepository;
        this.donHangRepository = donHangRepository;
    }

    public List<ThanhToanDTO> getAllThanhToan() {
        return thanhToanRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ThanhToanDTO getThanhToanById(Long id) {
        return thanhToanRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán với ID: " + id));
    }
    
    public List<ThanhToanDTO> getThanhToanByDonHang(Long idDonHang) {
        return thanhToanRepository.findByIdDonHangOrderByNgayThanhToanDesc(idDonHang).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ThanhToanDTO> getThanhToanByNguoiDung(Long idNguoiDung) {
        return thanhToanRepository.findByIdNguoiDungOrderByNgayThanhToanDesc(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ThanhToanDTO> getThanhToanByTrangThai(String trangThai) {
        ThanhToan.TrangThai trangThaiEnum = ThanhToan.TrangThai.valueOf(trangThai);
        return thanhToanRepository.findByTrangThai(trangThaiEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ThanhToanDTO> getThanhToanByLoai(String loaiThanhToan) {
        ThanhToan.LoaiThanhToan loaiEnum = ThanhToan.LoaiThanhToan.valueOf(loaiThanhToan);
        return thanhToanRepository.findByLoaiThanhToan(loaiEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ThanhToanDTO createThanhToan(ThanhToanDTO thanhToanDTO) {
        ThanhToan thanhToan = mapToEntity(thanhToanDTO);
        ThanhToan savedThanhToan = thanhToanRepository.save(thanhToan);
        
        // Cập nhật trạng thái đơn hàng dựa vào loại thanh toán
        DonHang donHang = thanhToan.getDonHang();
        
        if (thanhToan.getLoaiThanhToan() == ThanhToan.LoaiThanhToan.dat_coc &&
                thanhToan.getTrangThai() == ThanhToan.TrangThai.hoan_thanh) {
            donHang.setTrangThai(DonHang.TrangThai.da_dat_coc);
            donHangRepository.save(donHang);
        } else if (thanhToan.getLoaiThanhToan() == ThanhToan.LoaiThanhToan.toan_bo &&
                thanhToan.getTrangThai() == ThanhToan.TrangThai.hoan_thanh) {
            donHang.setTrangThai(DonHang.TrangThai.dang_xu_ly);
            donHangRepository.save(donHang);
        }
        
        return mapToDTO(savedThanhToan);
    }
    
    public ThanhToanDTO updateThanhToan(Long id, ThanhToanDTO thanhToanDTO) {
        ThanhToan existingThanhToan = thanhToanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán với ID: " + id));
        
        // Lưu trạng thái cũ để kiểm tra sự thay đổi
        ThanhToan.TrangThai oldTrangThai = existingThanhToan.getTrangThai();
        
        // Cập nhật thông tin thanh toán
        updateEntityFromDTO(existingThanhToan, thanhToanDTO);
        
        // Nếu trạng thái thanh toán thay đổi, cập nhật trạng thái đơn hàng
        if (oldTrangThai != existingThanhToan.getTrangThai() && 
                existingThanhToan.getTrangThai() == ThanhToan.TrangThai.hoan_thanh) {
            
            DonHang donHang = existingThanhToan.getDonHang();
            
            if (existingThanhToan.getLoaiThanhToan() == ThanhToan.LoaiThanhToan.dat_coc) {
                donHang.setTrangThai(DonHang.TrangThai.da_dat_coc);
                donHangRepository.save(donHang);
            } else if (existingThanhToan.getLoaiThanhToan() == ThanhToan.LoaiThanhToan.toan_bo) {
                donHang.setTrangThai(DonHang.TrangThai.dang_xu_ly);
                donHangRepository.save(donHang);
            }
        }
        
        return mapToDTO(thanhToanRepository.save(existingThanhToan));
    }
    
    public void deleteThanhToan(Long id) {
        thanhToanRepository.deleteById(id);
    }
    
    private ThanhToan mapToEntity(ThanhToanDTO dto) {
        ThanhToan thanhToan = new ThanhToan();
        
        if (dto.getId() != null) {
            thanhToan.setId(dto.getId());
        }
        
        // Set DonHang
        DonHang donHang = donHangRepository.findById(dto.getIdDonHang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + dto.getIdDonHang()));
        thanhToan.setDonHang(donHang);
        
        // Set các thông tin khác
        thanhToan.setSoTien(dto.getSoTien());
        
        if (dto.getLoaiThanhToan() != null) {
            thanhToan.setLoaiThanhToan(ThanhToan.LoaiThanhToan.valueOf(dto.getLoaiThanhToan()));
        }
        
        thanhToan.setPhuongThuc(dto.getPhuongThuc());
        thanhToan.setMaGiaoDich(dto.getMaGiaoDich());
        
        if (dto.getTrangThai() != null) {
            thanhToan.setTrangThai(ThanhToan.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        return thanhToan;
    }
    
    private void updateEntityFromDTO(ThanhToan thanhToan, ThanhToanDTO dto) {
        // Không thay đổi đơn hàng liên kết
        
        // Cập nhật các thông tin khác
        if (dto.getSoTien() != null) {
            thanhToan.setSoTien(dto.getSoTien());
        }
        
        if (dto.getLoaiThanhToan() != null) {
            thanhToan.setLoaiThanhToan(ThanhToan.LoaiThanhToan.valueOf(dto.getLoaiThanhToan()));
        }
        
        if (dto.getPhuongThuc() != null) {
            thanhToan.setPhuongThuc(dto.getPhuongThuc());
        }
        
        if (dto.getMaGiaoDich() != null) {
            thanhToan.setMaGiaoDich(dto.getMaGiaoDich());
        }
        
                if (dto.getTrangThai() != null) {
            thanhToan.setTrangThai(ThanhToan.TrangThai.valueOf(dto.getTrangThai()));
        }
    }
    
    private ThanhToanDTO mapToDTO(ThanhToan thanhToan) {
        ThanhToanDTO dto = new ThanhToanDTO();
        
        dto.setId(thanhToan.getId());
        
        // DonHang info
        if (thanhToan.getDonHang() != null) {
            dto.setIdDonHang(thanhToan.getDonHang().getId());
        }
        
        // Các thông tin khác
        dto.setSoTien(thanhToan.getSoTien());
        
        if (thanhToan.getLoaiThanhToan() != null) {
            dto.setLoaiThanhToan(thanhToan.getLoaiThanhToan().name());
        }
        
        if (thanhToan.getNgayThanhToan() != null) {
            dto.setNgayThanhToan(thanhToan.getNgayThanhToan().format(DATETIME_FORMATTER));
        }
        
        dto.setPhuongThuc(thanhToan.getPhuongThuc());
        dto.setMaGiaoDich(thanhToan.getMaGiaoDich());
        
        if (thanhToan.getTrangThai() != null) {
            dto.setTrangThai(thanhToan.getTrangThai().name());
        }
        
        return dto;
    }
}
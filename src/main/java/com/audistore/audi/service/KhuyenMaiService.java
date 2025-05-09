package com.audistore.audi.service;

import com.audistore.audi.dto.DieuKienKhuyenMaiDTO;
import com.audistore.audi.dto.KhuyenMaiDTO;
import com.audistore.audi.model.*;
import com.audistore.audi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiService {

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    
    @Autowired
    private DieuKienKhuyenMaiRepository dieuKienKhuyenMaiRepository;
    
    @Autowired
    private MauXeRepository mauXeRepository;
    
    @Autowired
    private DongXeRepository dongXeRepository;
    
    @Autowired
    private TuyChonRepository tuyChonRepository;
    
    @Transactional
    public KhuyenMaiDTO taoKhuyenMai(KhuyenMaiDTO dto) {
        // Kiểm tra mã khuyến mãi nếu có
        if (dto.getMaKhuyenMai() != null && !dto.getMaKhuyenMai().isEmpty()) {
            if (khuyenMaiRepository.findByMaKhuyenMai(dto.getMaKhuyenMai()).isPresent()) {
                throw new RuntimeException("Mã khuyến mãi đã tồn tại: " + dto.getMaKhuyenMai());
            }
        }
        
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setTen(dto.getTen());
        khuyenMai.setMoTa(dto.getMoTa());
        khuyenMai.setLoaiGiamGia(KhuyenMai.LoaiGiamGia.valueOf(dto.getLoaiGiamGia()));
        khuyenMai.setGiaTriGiam(dto.getGiaTriGiam());
        khuyenMai.setNgayBatDau(dto.getNgayBatDau());
        khuyenMai.setNgayKetThuc(dto.getNgayKetThuc());
        khuyenMai.setMaKhuyenMai(dto.getMaKhuyenMai());
        khuyenMai.setApDungCho(KhuyenMai.LoaiApDung.valueOf(dto.getApDungCho()));
        khuyenMai.setGiaTriToiThieu(dto.getGiaTriToiThieu());
        khuyenMai.setGioiHanSuDung(dto.getGioiHanSuDung());
        khuyenMai.setSoLanDaDung(0);
        
        KhuyenMai savedKhuyenMai = khuyenMaiRepository.save(khuyenMai);
        
        // Lưu các điều kiện khuyến mãi
        if (dto.getDanhSachDieuKien() != null && !dto.getDanhSachDieuKien().isEmpty()) {
            for (DieuKienKhuyenMaiDTO dieuKienDTO : dto.getDanhSachDieuKien()) {
                DieuKienKhuyenMai dieuKien = new DieuKienKhuyenMai();
                dieuKien.setKhuyenMai(savedKhuyenMai);
                dieuKien.setLoaiDoiTuong(DieuKienKhuyenMai.LoaiDoiTuong.valueOf(dieuKienDTO.getLoaiDoiTuong()));
                dieuKien.setIdDoiTuong(dieuKienDTO.getIdDoiTuong());
                
                // Kiểm tra tồn tại của đối tượng áp dụng
                kiemTraTonTaiDoiTuong(dieuKien.getLoaiDoiTuong(), dieuKien.getIdDoiTuong());
                
                dieuKienKhuyenMaiRepository.save(dieuKien);
            }
        }
        
        return convertToDTO(khuyenMaiRepository.findById(savedKhuyenMai.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi vừa tạo")));
    }
    
    public KhuyenMaiDTO getKhuyenMaiById(Long id) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        return convertToDTO(khuyenMai);
    }
    
    public KhuyenMaiDTO getKhuyenMaiByMa(String maKhuyenMai) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findByMaKhuyenMai(maKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với mã: " + maKhuyenMai));
        return convertToDTO(khuyenMai);
    }
    
    public Page<KhuyenMaiDTO> getAllKhuyenMai(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiRepository.findAll(pageable);
        return khuyenMaiPage.map(this::convertToDTO);
    }
    
    public Page<KhuyenMaiDTO> getKhuyenMaiConHieuLuc(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayKetThuc").ascending());
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiRepository.findAllActive(pageable);
        return khuyenMaiPage.map(this::convertToDTO);
    }
    
    public Page<KhuyenMaiDTO> getKhuyenMaiSapHetHan(int pageNo, int pageSize) {
        LocalDate now = LocalDate.now();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayKetThuc").ascending());
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiRepository.findByNgayKetThucGreaterThanEqual(now, pageable);
        return khuyenMaiPage.map(this::convertToDTO);
    }
    
    @Transactional
    public KhuyenMaiDTO capNhatKhuyenMai(Long id, KhuyenMaiDTO dto) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        
        // Kiểm tra mã khuyến mãi nếu có sự thay đổi
        if (dto.getMaKhuyenMai() != null && !dto.getMaKhuyenMai().equals(khuyenMai.getMaKhuyenMai())) {
            if (khuyenMaiRepository.findByMaKhuyenMai(dto.getMaKhuyenMai()).isPresent()) {
                throw new RuntimeException("Mã khuyến mãi đã tồn tại: " + dto.getMaKhuyenMai());
            }
        }
        
        khuyenMai.setTen(dto.getTen());
        khuyenMai.setMoTa(dto.getMoTa());
        khuyenMai.setLoaiGiamGia(KhuyenMai.LoaiGiamGia.valueOf(dto.getLoaiGiamGia()));
        khuyenMai.setGiaTriGiam(dto.getGiaTriGiam());
        khuyenMai.setNgayBatDau(dto.getNgayBatDau());
        khuyenMai.setNgayKetThuc(dto.getNgayKetThuc());
        khuyenMai.setMaKhuyenMai(dto.getMaKhuyenMai());
        khuyenMai.setApDungCho(KhuyenMai.LoaiApDung.valueOf(dto.getApDungCho()));
        khuyenMai.setGiaTriToiThieu(dto.getGiaTriToiThieu());
        khuyenMai.setGioiHanSuDung(dto.getGioiHanSuDung());
        
        KhuyenMai updatedKhuyenMai = khuyenMaiRepository.save(khuyenMai);
        
        // Cập nhật các điều kiện khuyến mãi
        if (dto.getDanhSachDieuKien() != null) {
            // Xóa các điều kiện hiện tại
            dieuKienKhuyenMaiRepository.deleteByKhuyenMaiId(id);
            
            // Thêm các điều kiện mới
            for (DieuKienKhuyenMaiDTO dieuKienDTO : dto.getDanhSachDieuKien()) {
                DieuKienKhuyenMai dieuKien = new DieuKienKhuyenMai();
                dieuKien.setKhuyenMai(updatedKhuyenMai);
                dieuKien.setLoaiDoiTuong(DieuKienKhuyenMai.LoaiDoiTuong.valueOf(dieuKienDTO.getLoaiDoiTuong()));
                dieuKien.setIdDoiTuong(dieuKienDTO.getIdDoiTuong());
                
                // Kiểm tra tồn tại của đối tượng áp dụng
                kiemTraTonTaiDoiTuong(dieuKien.getLoaiDoiTuong(), dieuKien.getIdDoiTuong());
                
                dieuKienKhuyenMaiRepository.save(dieuKien);
            }
        }
        
        return convertToDTO(khuyenMaiRepository.findById(updatedKhuyenMai.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi vừa cập nhật")));
    }
    
    @Transactional
    public void xoaKhuyenMai(Long id) {
        if (!khuyenMaiRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id);
        }
        
        // Xóa các điều kiện khuyến mãi trước
        dieuKienKhuyenMaiRepository.deleteByKhuyenMaiId(id);
        
        // Xóa khuyến mãi
        khuyenMaiRepository.deleteById(id);
    }
    
    public List<KhuyenMaiDTO> timKhuyenMaiChoMauXe(Long idMauXe) {
        List<KhuyenMai> danhSachKhuyenMai = new ArrayList<>();
        
        // Lấy ID dòng xe của mẫu xe
        MauXe mauXe = mauXeRepository.findById(idMauXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + idMauXe));
        Long idDongXe = mauXe.getDongXe().getId();
        
        // Lấy khuyến mãi áp dụng cho tất cả mẫu xe
        danhSachKhuyenMai.addAll(khuyenMaiRepository.findActiveForAllModels());
        
        // Lấy khuyến mãi áp dụng cho dòng xe cụ thể
        danhSachKhuyenMai.addAll(khuyenMaiRepository.findActiveForSeries(idDongXe));
        
        // Lấy khuyến mãi áp dụng cho mẫu xe cụ thể
        danhSachKhuyenMai.addAll(khuyenMaiRepository.findActiveForModel(idMauXe));
        
        // Loại bỏ các khuyến mãi trùng lặp
        List<KhuyenMai> danhSachKhongTrung = danhSachKhuyenMai.stream()
                .distinct()
                .collect(Collectors.toList());
        
        return danhSachKhongTrung.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void tangSoLanSuDung(Long idKhuyenMai) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + idKhuyenMai));
        
        khuyenMai.setSoLanDaDung(khuyenMai.getSoLanDaDung() + 1);
        khuyenMaiRepository.save(khuyenMai);
    }
    
    // Tính giá sau khi áp dụng khuyến mãi
    public BigDecimal tinhGiaSauKhuyenMai(BigDecimal giaBanDau, Long idKhuyenMai) {
        if (idKhuyenMai == null) {
            return giaBanDau;
        }
        
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + idKhuyenMai));
        
        if (!khuyenMai.conHieuLuc()) {
            throw new RuntimeException("Khuyến mãi đã hết hiệu lực");
        }
        
        if (!khuyenMai.hợpLeChoGiaTriDonHang(giaBanDau)) {
            throw new RuntimeException("Giá trị đơn hàng không đủ để áp dụng khuyến mãi");
        }
        
        switch (khuyenMai.getLoaiGiamGia()) {
            case phan_tram:
                BigDecimal phanTram = khuyenMai.getGiaTriGiam().divide(new BigDecimal(100));
                BigDecimal soTienGiam = giaBanDau.multiply(phanTram);
                return giaBanDau.subtract(soTienGiam);
                
            case so_tien_co_dinh:
                if (khuyenMai.getGiaTriGiam().compareTo(giaBanDau) >= 0) {
                    return BigDecimal.ZERO; // Không thể giảm nhiều hơn giá trị đơn hàng
                }
                return giaBanDau.subtract(khuyenMai.getGiaTriGiam());
                
            case tuy_chon_mien_phi:
                // Đối với tùy chọn miễn phí, giá không thay đổi vì đây là khuyến mãi khác
                return giaBanDau;
                
            default:
                return giaBanDau;
        }
    }
    
    private void kiemTraTonTaiDoiTuong(DieuKienKhuyenMai.LoaiDoiTuong loaiDoiTuong, Long idDoiTuong) {
        switch (loaiDoiTuong) {
            case mau_xe:
                if (!mauXeRepository.existsById(idDoiTuong)) {
                    throw new RuntimeException("Không tìm thấy mẫu xe với ID: " + idDoiTuong);
                }
                break;
                
            case dong_xe:
                if (!dongXeRepository.existsById(idDoiTuong)) {
                    throw new RuntimeException("Không tìm thấy dòng xe với ID: " + idDoiTuong);
                }
                break;
                
            case tuy_chon:
                if (!tuyChonRepository.existsById(idDoiTuong)) {
                    throw new RuntimeException("Không tìm thấy tùy chọn với ID: " + idDoiTuong);
                }
                break;
        }
    }
    
        private KhuyenMaiDTO convertToDTO(KhuyenMai entity) {
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        
        dto.setId(entity.getId());
        dto.setTen(entity.getTen());
        dto.setMoTa(entity.getMoTa());
        dto.setLoaiGiamGia(entity.getLoaiGiamGia().name());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setNgayBatDau(entity.getNgayBatDau());
        dto.setNgayKetThuc(entity.getNgayKetThuc());
        dto.setMaKhuyenMai(entity.getMaKhuyenMai());
        dto.setApDungCho(entity.getApDungCho().name());
        dto.setGiaTriToiThieu(entity.getGiaTriToiThieu());
        dto.setGioiHanSuDung(entity.getGioiHanSuDung());
        dto.setSoLanDaDung(entity.getSoLanDaDung());
        dto.setConHieuLuc(entity.conHieuLuc());
        
        // Chuyển đổi danh sách điều kiện
        List<DieuKienKhuyenMaiDTO> danhSachDieuKienDTO = new ArrayList<>();
        for (DieuKienKhuyenMai dieuKien : entity.getDanhSachDieuKien()) {
            DieuKienKhuyenMaiDTO dieuKienDTO = new DieuKienKhuyenMaiDTO();
            dieuKienDTO.setId(dieuKien.getId());
            dieuKienDTO.setIdKhuyenMai(entity.getId());
            dieuKienDTO.setLoaiDoiTuong(dieuKien.getLoaiDoiTuong().name());
            dieuKienDTO.setIdDoiTuong(dieuKien.getIdDoiTuong());
            
            // Thêm tên đối tượng cho dễ hiển thị
            dieuKienDTO.setTenDoiTuong(layTenDoiTuong(dieuKien.getLoaiDoiTuong(), dieuKien.getIdDoiTuong()));
            
            danhSachDieuKienDTO.add(dieuKienDTO);
        }
        
        dto.setDanhSachDieuKien(danhSachDieuKienDTO);
        
        return dto;
    }
    
    private String layTenDoiTuong(DieuKienKhuyenMai.LoaiDoiTuong loaiDoiTuong, Long idDoiTuong) {
        try {
            switch (loaiDoiTuong) {
                case mau_xe:
                    return mauXeRepository.findById(idDoiTuong)
                            .map(MauXe::getTenMau)
                            .orElse("Không tìm thấy");
                    
                case dong_xe:
                    return dongXeRepository.findById(idDoiTuong)
                            .map(DongXe::getTen)
                            .orElse("Không tìm thấy");
                    
                case tuy_chon:
                    return tuyChonRepository.findById(idDoiTuong)
                            .map(TuyChon::getTen)
                            .orElse("Không tìm thấy");
                    
                default:
                    return "Không xác định";
            }
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }
    
    // Kiểm tra khuyến mãi có áp dụng được với đơn hàng không
    public boolean kiemTraKhuyenMaiHopLe(Long idKhuyenMai, List<Long> idMauXes, BigDecimal tongGiaTri) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + idKhuyenMai));
        
        // Kiểm tra khuyến mãi còn hiệu lực không
        if (!khuyenMai.conHieuLuc()) {
            return false;
        }
        
        // Kiểm tra giá trị tối thiểu
        if (!khuyenMai.hợpLeChoGiaTriDonHang(tongGiaTri)) {
            return false;
        }
        
        // Kiểm tra đối tượng áp dụng
        switch (khuyenMai.getApDungCho()) {
            case tat_ca_mau:
                return true;
                
            case mau_cu_the:
                // Kiểm tra xem có mẫu xe nào trong danh sách đơn hàng khớp với điều kiện khuyến mãi không
                List<DieuKienKhuyenMai> dieuKienMauXe = khuyenMai.getDanhSachDieuKien().stream()
                        .filter(dk -> dk.getLoaiDoiTuong() == DieuKienKhuyenMai.LoaiDoiTuong.mau_xe)
                        .collect(Collectors.toList());
                
                for (Long idMauXe : idMauXes) {
                    for (DieuKienKhuyenMai dk : dieuKienMauXe) {
                        if (dk.getIdDoiTuong().equals(idMauXe)) {
                            return true;
                        }
                    }
                }
                return false;
                
            case dong_cu_the:
                // Lấy danh sách ID dòng xe từ danh sách mẫu xe
                List<Long> idDongXes = new ArrayList<>();
                for (Long idMauXe : idMauXes) {
                    MauXe mauXe = mauXeRepository.findById(idMauXe)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + idMauXe));
                    idDongXes.add(mauXe.getDongXe().getId());
                }
                
                // Kiểm tra xem có dòng xe nào trong danh sách đơn hàng khớp với điều kiện khuyến mãi không
                List<DieuKienKhuyenMai> dieuKienDongXe = khuyenMai.getDanhSachDieuKien().stream()
                        .filter(dk -> dk.getLoaiDoiTuong() == DieuKienKhuyenMai.LoaiDoiTuong.dong_xe)
                        .collect(Collectors.toList());
                
                for (Long idDongXe : idDongXes) {
                    for (DieuKienKhuyenMai dk : dieuKienDongXe) {
                        if (dk.getIdDoiTuong().equals(idDongXe)) {
                            return true;
                        }
                    }
                }
                return false;
                
            default:
                return false;
        }
    }
}
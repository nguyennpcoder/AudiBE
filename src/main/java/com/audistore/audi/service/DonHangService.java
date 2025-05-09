package com.audistore.audi.service;

import com.audistore.audi.dto.ChiTietDonHangDTO;
import com.audistore.audi.dto.DonHangDTO;
import com.audistore.audi.model.*;
import com.audistore.audi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final TonKhoRepository tonKhoRepository;
    private final DaiLyRepository daiLyRepository;
    private final CauHinhTuyChinhRepository cauHinhTuyChinhRepository;
    private final KhuyenMaiService khuyenMaiService;
    private final KhuyenMaiRepository khuyenMaiRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public DonHangService(
            DonHangRepository donHangRepository,
            NguoiDungRepository nguoiDungRepository,
            TonKhoRepository tonKhoRepository,
            DaiLyRepository daiLyRepository,
            CauHinhTuyChinhRepository cauHinhTuyChinhRepository,
            KhuyenMaiService khuyenMaiService,
            KhuyenMaiRepository khuyenMaiRepository) {
        this.donHangRepository = donHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.daiLyRepository = daiLyRepository;
        this.cauHinhTuyChinhRepository = cauHinhTuyChinhRepository;
        this.khuyenMaiService = khuyenMaiService;
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    public List<DonHangDTO> getAllDonHang() {
        return donHangRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DonHangDTO getDonHangById(Long id) {
        return donHangRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
    }
    
    public List<DonHangDTO> getDonHangByNguoiDung(Long idNguoiDung) {
        return donHangRepository.findByIdNguoiDungOrderByNgayDatDesc(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DonHangDTO> getDonHangByDaiLy(Long idDaiLy) {
        return donHangRepository.findByIdDaiLyOrderByNgayDatDesc(idDaiLy).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DonHangDTO> getDonHangByTrangThai(String trangThai) {
        DonHang.TrangThai trangThaiEnum = DonHang.TrangThai.valueOf(trangThai);
        return donHangRepository.findByTrangThai(trangThaiEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public DonHangDTO createDonHang(DonHangDTO donHangDTO) {
        DonHang donHang = mapToEntity(donHangDTO);
        
        // Nếu đơn hàng có tonKho, cập nhật trạng thái của tonKho thành "đã đặt"
        if (donHang.getTonKho() != null) {
            TonKho tonKho = donHang.getTonKho();
            tonKho.setTrangThai(TonKho.TrangThai.da_dat);
            tonKhoRepository.save(tonKho);
        }
        
        // Áp dụng khuyến mãi nếu có
        if (donHangDTO.getIdKhuyenMai() != null) {
            // Lấy danh sách ID mẫu xe trong đơn hàng
            List<Long> idMauXes = donHangDTO.getChiTietDonHangDTOs().stream()
                    .map(ChiTietDonHangDTO::getIdMauXe)
                    .collect(Collectors.toList());
            
            // Kiểm tra khuyến mãi có hợp lệ không
            if (!khuyenMaiService.kiemTraKhuyenMaiHopLe(
                    donHangDTO.getIdKhuyenMai(), idMauXes, donHangDTO.getTongTien())) {
                throw new RuntimeException("Khuyến mãi không áp dụng được cho đơn hàng này");
            }
            
            // Tính giá sau khuyến mãi
            BigDecimal tongTienSauKhuyenMai = khuyenMaiService.tinhGiaSauKhuyenMai(
                    donHangDTO.getTongTien(), donHangDTO.getIdKhuyenMai());
            
            // Cập nhật giá trị đơn hàng
            donHang.setTongTien(tongTienSauKhuyenMai);
            
            // Thêm thông tin khuyến mãi vào đơn hàng
            KhuyenMai khuyenMai = khuyenMaiRepository.findById(donHangDTO.getIdKhuyenMai())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
            donHang.setKhuyenMai(khuyenMai);
            donHang.setTienGiam(donHangDTO.getTongTien().subtract(tongTienSauKhuyenMai));
            
            // Tăng số lần sử dụng khuyến mãi
            khuyenMaiService.tangSoLanSuDung(donHangDTO.getIdKhuyenMai());
        }
        
        return mapToDTO(donHangRepository.save(donHang));
    }
    
    public DonHangDTO updateDonHang(Long id, DonHangDTO donHangDTO) {
        DonHang existingDonHang = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        
        // Lưu trạng thái cũ để kiểm tra sự thay đổi
        DonHang.TrangThai oldTrangThai = existingDonHang.getTrangThai();
        
        // Cập nhật thông tin đơn hàng
        updateEntityFromDTO(existingDonHang, donHangDTO);
        
        // Nếu trạng thái đơn hàng thay đổi thành "đã giao", cập nhật trạng thái của tonKho
        if (oldTrangThai != existingDonHang.getTrangThai() && 
                existingDonHang.getTrangThai() == DonHang.TrangThai.da_giao && 
                existingDonHang.getTonKho() != null) {
            TonKho tonKho = existingDonHang.getTonKho();
            tonKho.setTrangThai(TonKho.TrangThai.da_ban);
            tonKhoRepository.save(tonKho);
        }
        
        // Nếu trạng thái đơn hàng thay đổi thành "đã hủy", cập nhật trạng thái của tonKho về "có sẵn"
        if (oldTrangThai != existingDonHang.getTrangThai() && 
                existingDonHang.getTrangThai() == DonHang.TrangThai.da_huy && 
                existingDonHang.getTonKho() != null) {
            TonKho tonKho = existingDonHang.getTonKho();
            tonKho.setTrangThai(TonKho.TrangThai.co_san);
            tonKhoRepository.save(tonKho);
        }
        
        return mapToDTO(donHangRepository.save(existingDonHang));
    }
    
    public void deleteDonHang(Long id) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        
        // Cập nhật lại trạng thái tồn kho nếu đơn hàng bị xóa
        if (donHang.getTonKho() != null) {
            TonKho tonKho = donHang.getTonKho();
            tonKho.setTrangThai(TonKho.TrangThai.co_san);
            tonKhoRepository.save(tonKho);
        }
        
        donHangRepository.deleteById(id);
    }
    
    private DonHang mapToEntity(DonHangDTO dto) {
        DonHang donHang = new DonHang();
        
        if (dto.getId() != null) {
            donHang.setId(dto.getId());
        }
        
        // Set NguoiDung
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        donHang.setNguoiDung(nguoiDung);
        
        // Set TonKho nếu có
        if (dto.getIdTonKho() != null) {
            TonKho tonKho = tonKhoRepository.findById(dto.getIdTonKho())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + dto.getIdTonKho()));
            donHang.setTonKho(tonKho);
        }
        
        // Set CauHinh nếu có
        if (dto.getIdCauHinh() != null) {
            CauHinhTuyChiNh cauHinh = cauHinhTuyChinhRepository.findById(dto.getIdCauHinh())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình với ID: " + dto.getIdCauHinh()));
            donHang.setCauHinh(cauHinh);
        }
        
        // Set DaiLy
        DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
        donHang.setDaiLy(daiLy);
        
        // Set các thông tin khác
        if (dto.getTrangThai() != null) {
            donHang.setTrangThai(DonHang.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        donHang.setTienDatCoc(dto.getTienDatCoc());
        donHang.setTongTien(dto.getTongTien());
        
        if (dto.getNgayGiaoDuKien() != null && !dto.getNgayGiaoDuKien().isEmpty()) {
            donHang.setNgayGiaoDuKien(LocalDate.parse(dto.getNgayGiaoDuKien(), DATE_FORMATTER));
        }
        
        if (dto.getNgayGiaoThucTe() != null && !dto.getNgayGiaoThucTe().isEmpty()) {
            donHang.setNgayGiaoThucTe(LocalDate.parse(dto.getNgayGiaoThucTe(), DATE_FORMATTER));
        }
        
        donHang.setPhuongThucThanhToan(dto.getPhuongThucThanhToan());
        donHang.setGhiChu(dto.getGhiChu());
        
        // Set khuyến mãi nếu có
        if (dto.getIdKhuyenMai() != null) {
            KhuyenMai khuyenMai = khuyenMaiRepository.findById(dto.getIdKhuyenMai())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + dto.getIdKhuyenMai()));
            donHang.setKhuyenMai(khuyenMai);
            
            // Tính tiền giảm nếu chưa có
            if (dto.getTienGiam() == null) {
                BigDecimal giaSauKhuyenMai = khuyenMaiService.tinhGiaSauKhuyenMai(dto.getTongTien(), dto.getIdKhuyenMai());
                donHang.setTienGiam(dto.getTongTien().subtract(giaSauKhuyenMai));
            } else {
                donHang.setTienGiam(dto.getTienGiam());
            }
        }
        
        return donHang;
    }
    
    private void updateEntityFromDTO(DonHang donHang, DonHangDTO dto) {
        // Không cập nhật NguoiDung vì không thể đổi chủ đơn hàng
        
        // Cập nhật TonKho nếu có thay đổi
        if (dto.getIdTonKho() != null && 
                (donHang.getTonKho() == null || !dto.getIdTonKho().equals(donHang.getTonKho().getId()))) {
            TonKho tonKho = tonKhoRepository.findById(dto.getIdTonKho())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + dto.getIdTonKho()));
            donHang.setTonKho(tonKho);
        }
        
        // Cập nhật CauHinh nếu có thay đổi
        if (dto.getIdCauHinh() != null && 
                (donHang.getCauHinh() == null || !dto.getIdCauHinh().equals(donHang.getCauHinh().getId()))) {
            CauHinhTuyChiNh cauHinh = cauHinhTuyChinhRepository.findById(dto.getIdCauHinh())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình với ID: " + dto.getIdCauHinh()));
            donHang.setCauHinh(cauHinh);
        }
        
        // Cập nhật DaiLy nếu có thay đổi
        if (dto.getIdDaiLy() != null && !dto.getIdDaiLy().equals(donHang.getDaiLy().getId())) {
            DaiLy daiLy = daiLyRepository.findById(dto.getIdDaiLy())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID: " + dto.getIdDaiLy()));
            donHang.setDaiLy(daiLy);
        }
        
        // Cập nhật các thông tin khác
        if (dto.getTrangThai() != null) {
            donHang.setTrangThai(DonHang.TrangThai.valueOf(dto.getTrangThai()));
        }
        
        if (dto.getTienDatCoc() != null) {
            donHang.setTienDatCoc(dto.getTienDatCoc());
        }
        
        if (dto.getTongTien() != null) {
            donHang.setTongTien(dto.getTongTien());
        }
        
        if (dto.getNgayGiaoDuKien() != null && !dto.getNgayGiaoDuKien().isEmpty()) {
            donHang.setNgayGiaoDuKien(LocalDate.parse(dto.getNgayGiaoDuKien(), DATE_FORMATTER));
        }
        
        if (dto.getNgayGiaoThucTe() != null && !dto.getNgayGiaoThucTe().isEmpty()) {
            donHang.setNgayGiaoThucTe(LocalDate.parse(dto.getNgayGiaoThucTe(), DATE_FORMATTER));
        }
        
        if (dto.getPhuongThucThanhToan() != null) {
            donHang.setPhuongThucThanhToan(dto.getPhuongThucThanhToan());
        }
        
        if (dto.getGhiChu() != null) {
            donHang.setGhiChu(dto.getGhiChu());
        }
    }
    
    private DonHangDTO mapToDTO(DonHang donHang) {
        DonHangDTO dto = new DonHangDTO();
        
        dto.setId(donHang.getId());
        
        // Nguoi dung
        dto.setIdNguoiDung(donHang.getNguoiDung().getId());
        dto.setTenNguoiDung(donHang.getNguoiDung().getTen());
        
        // Ton kho
        if (donHang.getTonKho() != null) {
            dto.setIdTonKho(donHang.getTonKho().getId());
        }
        
        // Cau hinh
        if (donHang.getCauHinh() != null) {
            dto.setIdCauHinh(donHang.getCauHinh().getId());
        }
        
        // Dai ly
        dto.setIdDaiLy(donHang.getDaiLy().getId());
        dto.setTenDaiLy(donHang.getDaiLy().getTen());
        
        // Ngay dat
        if (donHang.getNgayDat() != null) {
            dto.setNgayDat(donHang.getNgayDat().format(DATETIME_FORMATTER));
        }
        
        // Trang thai
        dto.setTrangThai(donHang.getTrangThai().name());
        
        // Thong tin khac
        dto.setTienDatCoc(donHang.getTienDatCoc());
        dto.setTongTien(donHang.getTongTien());
        
        if (donHang.getNgayGiaoDuKien() != null) {
            dto.setNgayGiaoDuKien(donHang.getNgayGiaoDuKien().format(DATE_FORMATTER));
        }
        
        if (donHang.getNgayGiaoThucTe() != null) {
            dto.setNgayGiaoThucTe(donHang.getNgayGiaoThucTe().format(DATE_FORMATTER));
        }
        
        dto.setPhuongThucThanhToan(donHang.getPhuongThucThanhToan());
        dto.setGhiChu(donHang.getGhiChu());
        
        // Thông tin khuyến mãi
        if (donHang.getKhuyenMai() != null) {
            dto.setIdKhuyenMai(donHang.getKhuyenMai().getId());
            dto.setTenKhuyenMai(donHang.getKhuyenMai().getTen());
            dto.setTienGiam(donHang.getTienGiam());
        }
        
        return dto;
    }
}
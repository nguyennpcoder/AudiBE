package com.audistore.audi.service;

import com.audistore.audi.dto.HoSoTraGopDTO;
import com.audistore.audi.model.DonHang;
import com.audistore.audi.model.HoSoTraGop;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.DonHangRepository;
import com.audistore.audi.repository.HoSoTraGopRepository;
import com.audistore.audi.repository.NguoiDungRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class HoSoTraGopService {

    private final HoSoTraGopRepository hoSoTraGopRepository;
    private final DonHangRepository donHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public HoSoTraGopService(HoSoTraGopRepository hoSoTraGopRepository, DonHangRepository donHangRepository, NguoiDungRepository nguoiDungRepository) {
        this.hoSoTraGopRepository = hoSoTraGopRepository;
        this.donHangRepository = donHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    public List<HoSoTraGopDTO> getAllHoSoTraGop() {
        return hoSoTraGopRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<HoSoTraGopDTO> getAllHoSoTraGop(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return hoSoTraGopRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    public HoSoTraGopDTO getHoSoTraGopById(Long id) {
        return hoSoTraGopRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ trả góp với ID: " + id));
    }

    public List<HoSoTraGopDTO> getHoSoTraGopByDonHang(Long idDonHang) {
        return hoSoTraGopRepository.findByDonHangId(idDonHang).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<HoSoTraGopDTO> getHoSoTraGopByNguoiDung(Long idNguoiDung) {
        return hoSoTraGopRepository.findByNguoiDungId(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<HoSoTraGopDTO> getHoSoTraGopByTrangThai(String trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayNopHoSo").descending());
        return hoSoTraGopRepository.findByTrangThai(trangThai, pageable)
                .map(this::mapToDTO);
    }

    public HoSoTraGopDTO createHoSoTraGop(HoSoTraGopDTO hoSoTraGopDTO, Long currentUserId) {
        // Kiểm tra đơn hàng tồn tại
        DonHang donHang = donHangRepository.findById(hoSoTraGopDTO.getIdDonHang())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + hoSoTraGopDTO.getIdDonHang()));
        
        // Kiểm tra quyền - người dùng hiện tại phải là chủ đơn hàng hoặc admin/nhân viên bán hàng
        NguoiDung currentUser = nguoiDungRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin người dùng"));
        
        boolean isAdmin = currentUser.getVaiTro().equals("quan_tri") || currentUser.getVaiTro().equals("ban_hang");
        boolean isOwner = donHang.getNguoiDung().getId().equals(currentUserId);
        
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Bạn không có quyền tạo hồ sơ trả góp cho đơn hàng này");
        }
        
        // Kiểm tra đơn hàng đã có hồ sơ trả góp chưa
        List<HoSoTraGop> hoSoCu = hoSoTraGopRepository.findByDonHangId(hoSoTraGopDTO.getIdDonHang());
        for (HoSoTraGop hoSo : hoSoCu) {
            if (hoSo.getTrangThai().equals(HoSoTraGop.TrangThai.dang_xu_ly) || 
                hoSo.getTrangThai().equals(HoSoTraGop.TrangThai.da_phe_duyet)) {
                throw new IllegalStateException("Đơn hàng này đã có hồ sơ trả góp đang xử lý hoặc đã được phê duyệt");
            }
        }
        
        // Tạo hồ sơ mới
        HoSoTraGop hoSoTraGop = mapToEntity(hoSoTraGopDTO);
        hoSoTraGop.setDonHang(donHang);
        hoSoTraGop.setNgayNopHoSo(LocalDateTime.now());
        hoSoTraGop.setTrangThai(HoSoTraGop.TrangThai.dang_xu_ly);
        
        // Lưu hồ sơ
        HoSoTraGop savedHoSo = hoSoTraGopRepository.save(hoSoTraGop);
        
        return mapToDTO(savedHoSo);
    }

    public HoSoTraGopDTO updateHoSoTraGop(Long id, HoSoTraGopDTO hoSoTraGopDTO) {
        HoSoTraGop existingHoSo = hoSoTraGopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ trả góp với ID: " + id));
        
        // Kiểm tra trạng thái
        if (existingHoSo.getTrangThai() == HoSoTraGop.TrangThai.hoan_thanh) {
            throw new IllegalStateException("Không thể cập nhật hồ sơ trả góp đã hoàn thành");
        }
        
        // Chỉ cập nhật các trường có thể thay đổi
        if (hoSoTraGopDTO.getNganHangDoiTac() != null) {
            existingHoSo.setNganHangDoiTac(hoSoTraGopDTO.getNganHangDoiTac());
        }
        
        if (hoSoTraGopDTO.getSoTienVay() != null) {
            existingHoSo.setSoTienVay(hoSoTraGopDTO.getSoTienVay());
        }
        
        if (hoSoTraGopDTO.getLaiSuat() != null) {
            existingHoSo.setLaiSuat(hoSoTraGopDTO.getLaiSuat());
        }
        
        if (hoSoTraGopDTO.getKyHanThang() != null) {
            existingHoSo.setKyHanThang(hoSoTraGopDTO.getKyHanThang());
        }
        
        if (hoSoTraGopDTO.getTraHangThang() != null) {
            existingHoSo.setTraHangThang(hoSoTraGopDTO.getTraHangThang());
        }
        
        if (hoSoTraGopDTO.getGhiChu() != null) {
            existingHoSo.setGhiChu(hoSoTraGopDTO.getGhiChu());
        }
        
        // Thông tin người mua hộ
        if (hoSoTraGopDTO.getNguoiMuaHo() != null) {
            existingHoSo.setNguoiMuaHo(hoSoTraGopDTO.getNguoiMuaHo());
        }
        
        if (hoSoTraGopDTO.getSoDienThoaiNguoiMuaHo() != null) {
            existingHoSo.setSoDienThoaiNguoiMuaHo(hoSoTraGopDTO.getSoDienThoaiNguoiMuaHo());
        }
        
        if (hoSoTraGopDTO.getEmailNguoiMuaHo() != null) {
            existingHoSo.setEmailNguoiMuaHo(hoSoTraGopDTO.getEmailNguoiMuaHo());
        }
        
        if (hoSoTraGopDTO.getDiaChiNguoiMuaHo() != null) {
            existingHoSo.setDiaChiNguoiMuaHo(hoSoTraGopDTO.getDiaChiNguoiMuaHo());
        }
        
        return mapToDTO(hoSoTraGopRepository.save(existingHoSo));
    }
    
    public HoSoTraGopDTO pheDuyetHoSo(Long id) {
        HoSoTraGop hoSo = hoSoTraGopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ trả góp với ID: " + id));
        
        if (hoSo.getTrangThai() != HoSoTraGop.TrangThai.dang_xu_ly) {
            throw new IllegalStateException("Chỉ có thể phê duyệt hồ sơ đang xử lý");
        }
        
        hoSo.setTrangThai(HoSoTraGop.TrangThai.da_phe_duyet);
        hoSo.setNgayQuyetDinh(LocalDateTime.now());
        
        return mapToDTO(hoSoTraGopRepository.save(hoSo));
    }
    
    public HoSoTraGopDTO tuChoiHoSo(Long id, String lyDo) {
        HoSoTraGop hoSo = hoSoTraGopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ trả góp với ID: " + id));
        
        if (hoSo.getTrangThai() != HoSoTraGop.TrangThai.dang_xu_ly) {
            throw new IllegalStateException("Chỉ có thể từ chối hồ sơ đang xử lý");
        }
        
        hoSo.setTrangThai(HoSoTraGop.TrangThai.bi_tu_choi);
        hoSo.setLyDoTuChoi(lyDo);
        hoSo.setNgayQuyetDinh(LocalDateTime.now());
        
        return mapToDTO(hoSoTraGopRepository.save(hoSo));
    }
    
    public HoSoTraGopDTO hoanThanhHoSo(Long id) {
        HoSoTraGop hoSo = hoSoTraGopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ trả góp với ID: " + id));
        
        if (hoSo.getTrangThai() != HoSoTraGop.TrangThai.da_phe_duyet) {
            throw new IllegalStateException("Chỉ có thể hoàn thành hồ sơ đã phê duyệt");
        }
        
        hoSo.setTrangThai(HoSoTraGop.TrangThai.hoan_thanh);
        
        return mapToDTO(hoSoTraGopRepository.save(hoSo));
    }
    
    public Map<String, Long> getThongKeHoSoTraGop() {
        Map<String, Long> thongKe = new HashMap<>();
        
        thongKe.put("tongSoHoSo", hoSoTraGopRepository.count());
        thongKe.put("dangXuLy", hoSoTraGopRepository.countByTrangThai("dang_xu_ly"));
        thongKe.put("daDuyet", hoSoTraGopRepository.countByTrangThai("da_phe_duyet"));
        thongKe.put("biTuChoi", hoSoTraGopRepository.countByTrangThai("bi_tu_choi"));
        thongKe.put("hoanThanh", hoSoTraGopRepository.countByTrangThai("hoan_thanh"));
        
        return thongKe;
    }

    public void deleteHoSoTraGop(Long id) {
        HoSoTraGop hoSo = hoSoTraGopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ trả góp với ID: " + id));
        
        // Chỉ cho phép xóa hồ sơ bị từ chối hoặc đang xử lý
        if (hoSo.getTrangThai() == HoSoTraGop.TrangThai.da_phe_duyet || 
            hoSo.getTrangThai() == HoSoTraGop.TrangThai.hoan_thanh) {
            throw new IllegalStateException("Không thể xóa hồ sơ trả góp đã phê duyệt hoặc hoàn thành");
        }
        
        hoSoTraGopRepository.deleteById(id);
    }

    private HoSoTraGopDTO mapToDTO(HoSoTraGop hoSoTraGop) {
        HoSoTraGopDTO dto = new HoSoTraGopDTO();
        dto.setId(hoSoTraGop.getId());
        dto.setIdDonHang(hoSoTraGop.getDonHang().getId());
        
        // Thông tin trả góp
        dto.setNganHangDoiTac(hoSoTraGop.getNganHangDoiTac());
        dto.setSoTienVay(hoSoTraGop.getSoTienVay());
        dto.setLaiSuat(hoSoTraGop.getLaiSuat());
        dto.setKyHanThang(hoSoTraGop.getKyHanThang());
        dto.setTraHangThang(hoSoTraGop.getTraHangThang());
        dto.setTrangThai(hoSoTraGop.getTrangThai().name());
        dto.setNgayNopHoSo(hoSoTraGop.getNgayNopHoSo());
        dto.setNgayQuyetDinh(hoSoTraGop.getNgayQuyetDinh());
        dto.setGhiChu(hoSoTraGop.getGhiChu());
        dto.setLyDoTuChoi(hoSoTraGop.getLyDoTuChoi());
        
        // Thông tin người mua hộ
        dto.setNguoiMuaHo(hoSoTraGop.getNguoiMuaHo());
        dto.setSoDienThoaiNguoiMuaHo(hoSoTraGop.getSoDienThoaiNguoiMuaHo());
        dto.setEmailNguoiMuaHo(hoSoTraGop.getEmailNguoiMuaHo());
        dto.setDiaChiNguoiMuaHo(hoSoTraGop.getDiaChiNguoiMuaHo());
        
        // Thông tin khách hàng và đơn hàng
        DonHang donHang = hoSoTraGop.getDonHang();
        NguoiDung nguoiDung = donHang.getNguoiDung();
        
        dto.setTenKhachHang(nguoiDung.getHo() + " " + nguoiDung.getTen());
        dto.setEmailKhachHang(nguoiDung.getEmail());
        dto.setSoDienThoaiKhachHang(nguoiDung.getSoDienThoai());
        dto.setTongGiaTriDonHang(donHang.getTongTien());
        
        // Tên mẫu xe
        MauXe mauXe = null;
        if (donHang.getCauHinh() != null) {
            mauXe = donHang.getCauHinh().getMauXe();
        } else if (donHang.getTonKho() != null) {
            mauXe = donHang.getTonKho().getMauXe();
        }
        
        if (mauXe != null) {
            dto.setTenMauXe(mauXe.getTenMau() + " " + mauXe.getNamSanXuat());
        }
        
        return dto;
    }

    private HoSoTraGop mapToEntity(HoSoTraGopDTO dto) {
        HoSoTraGop hoSoTraGop = new HoSoTraGop();
        
        if (dto.getId() != null) {
            hoSoTraGop.setId(dto.getId());
        }
        
        // Thông tin trả góp
        hoSoTraGop.setNganHangDoiTac(dto.getNganHangDoiTac());
        hoSoTraGop.setSoTienVay(dto.getSoTienVay());
        hoSoTraGop.setLaiSuat(dto.getLaiSuat());
        hoSoTraGop.setKyHanThang(dto.getKyHanThang());
        hoSoTraGop.setTraHangThang(dto.getTraHangThang());
        
        if (dto.getTrangThai() != null) {
            hoSoTraGop.setTrangThai(HoSoTraGop.TrangThai.valueOf(dto.getTrangThai()));
        } else {
            hoSoTraGop.setTrangThai(HoSoTraGop.TrangThai.dang_xu_ly);
        }
        
        hoSoTraGop.setGhiChu(dto.getGhiChu());
        hoSoTraGop.setLyDoTuChoi(dto.getLyDoTuChoi());
        
        // Thông tin người mua hộ
        hoSoTraGop.setNguoiMuaHo(dto.getNguoiMuaHo());
        hoSoTraGop.setSoDienThoaiNguoiMuaHo(dto.getSoDienThoaiNguoiMuaHo());
        hoSoTraGop.setEmailNguoiMuaHo(dto.getEmailNguoiMuaHo());
        hoSoTraGop.setDiaChiNguoiMuaHo(dto.getDiaChiNguoiMuaHo());
        
        return hoSoTraGop;
    }
}
package com.audistore.audi.service;

import com.audistore.audi.dto.PhanHoiYeuCauDTO;
import com.audistore.audi.dto.YeuCauHoTroDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.model.PhanHoiYeuCau;
import com.audistore.audi.model.YeuCauHoTro;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.repository.PhanHoiYeuCauRepository;
import com.audistore.audi.repository.YeuCauHoTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YeuCauHoTroService {

    @Autowired
    private YeuCauHoTroRepository yeuCauHoTroRepository;

    @Autowired
    private PhanHoiYeuCauRepository phanHoiYeuCauRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public YeuCauHoTroDTO taoYeuCauHoTro(YeuCauHoTroDTO dto) {
        YeuCauHoTro yeuCauHoTro = new YeuCauHoTro();
        
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        yeuCauHoTro.setNguoiDung(nguoiDung);
        
        yeuCauHoTro.setTieuDe(dto.getTieuDe());
        yeuCauHoTro.setNoiDung(dto.getNoiDung());
        
        if (dto.getMucDoUuTien() != null) {
            yeuCauHoTro.setMucDoUuTien(YeuCauHoTro.MucDoUuTien.valueOf(dto.getMucDoUuTien()));
        }
        
        YeuCauHoTro savedYeuCau = yeuCauHoTroRepository.save(yeuCauHoTro);
        return convertToDTO(savedYeuCau);
    }
    
    public YeuCauHoTroDTO getYeuCauById(Long id) {
        YeuCauHoTro yeuCauHoTro = yeuCauHoTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        
        YeuCauHoTroDTO dto = convertToDTO(yeuCauHoTro);
        
        // Lấy danh sách phản hồi
        List<PhanHoiYeuCau> phanHoiList = phanHoiYeuCauRepository.findByYeuCauHoTroIdOrderByNgayTaoAsc(id);
        List<PhanHoiYeuCauDTO> phanHoiDTOList = phanHoiList.stream()
                .map(this::convertPhanHoiToDTO)
                .collect(Collectors.toList());
        
        dto.setDanhSachPhanHoi(phanHoiDTOList);
        
        return dto;
    }
    
    public Page<YeuCauHoTroDTO> getAllYeuCau(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<YeuCauHoTro> yeuCauPage = yeuCauHoTroRepository.findAll(pageable);
        return yeuCauPage.map(this::convertToDTO);
    }
    
    public Page<YeuCauHoTroDTO> getYeuCauByNguoiDung(Long idNguoiDung, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").descending());
        Page<YeuCauHoTro> yeuCauPage = yeuCauHoTroRepository.findByNguoiDungId(idNguoiDung, pageable);
        return yeuCauPage.map(this::convertToDTO);
    }
    
    public Page<YeuCauHoTroDTO> getYeuCauByTrangThai(String trangThai, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").descending());
        Page<YeuCauHoTro> yeuCauPage = yeuCauHoTroRepository.findByTrangThai(
                YeuCauHoTro.TrangThai.valueOf(trangThai), pageable);
        return yeuCauPage.map(this::convertToDTO);
    }
    
    public Page<YeuCauHoTroDTO> getYeuCauByNguoiPhuTrach(Long idNguoiPhuTrach, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").descending());
        Page<YeuCauHoTro> yeuCauPage = yeuCauHoTroRepository.findByNguoiPhuTrachId(idNguoiPhuTrach, pageable);
        return yeuCauPage.map(this::convertToDTO);
    }
    
    public Page<YeuCauHoTroDTO> timKiemYeuCau(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayTao").descending());
        Page<YeuCauHoTro> yeuCauPage = yeuCauHoTroRepository.findByTieuDeContainingIgnoreCase(keyword, pageable);
        return yeuCauPage.map(this::convertToDTO);
    }
    
    @Transactional
    public YeuCauHoTroDTO capNhatTrangThai(Long id, String trangThai, Long idNguoiCapNhat) {
        YeuCauHoTro yeuCauHoTro = yeuCauHoTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        
        // Chỉ người hỗ trợ hoặc quản trị mới có thể thay đổi trạng thái
        NguoiDung nguoiCapNhat = nguoiDungRepository.findById(idNguoiCapNhat)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiCapNhat));
        
        boolean isAdmin = nguoiCapNhat.getVaiTro() == NguoiDung.VaiTro.quan_tri;
        boolean isSupport = nguoiCapNhat.getVaiTro() == NguoiDung.VaiTro.ho_tro;
        boolean isAssigned = yeuCauHoTro.getNguoiPhuTrach() != null && 
                            yeuCauHoTro.getNguoiPhuTrach().getId().equals(idNguoiCapNhat);
        
        if (!isAdmin && !isSupport && !isAssigned) {
            throw new AccessDeniedException("Không có quyền cập nhật trạng thái yêu cầu hỗ trợ");
        }
        
        yeuCauHoTro.setTrangThai(YeuCauHoTro.TrangThai.valueOf(trangThai));
        YeuCauHoTro updatedYeuCau = yeuCauHoTroRepository.save(yeuCauHoTro);
        return convertToDTO(updatedYeuCau);
    }
    
    @Transactional
    public YeuCauHoTroDTO capNhatUuTien(Long id, String mucDoUuTien) {
        YeuCauHoTro yeuCauHoTro = yeuCauHoTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        
        yeuCauHoTro.setMucDoUuTien(YeuCauHoTro.MucDoUuTien.valueOf(mucDoUuTien));
        YeuCauHoTro updatedYeuCau = yeuCauHoTroRepository.save(yeuCauHoTro);
        return convertToDTO(updatedYeuCau);
    }
    
    @Transactional
    public YeuCauHoTroDTO phanCongNguoiPhuTrach(Long id, Long idNguoiPhuTrach) {
        YeuCauHoTro yeuCauHoTro = yeuCauHoTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        
        NguoiDung nguoiPhuTrach = nguoiDungRepository.findById(idNguoiPhuTrach)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiPhuTrach));
        
        // Kiểm tra xem người được phân công có phải là nhân viên hỗ trợ hoặc admin không
        if (nguoiPhuTrach.getVaiTro() != NguoiDung.VaiTro.ho_tro && 
            nguoiPhuTrach.getVaiTro() != NguoiDung.VaiTro.quan_tri) {
            throw new RuntimeException("Người được phân công phải là nhân viên hỗ trợ hoặc quản trị viên");
        }
        
        yeuCauHoTro.setNguoiPhuTrach(nguoiPhuTrach);
        
        // Nếu trạng thái là "mới" thì cập nhật thành "đang xử lý"
        if (yeuCauHoTro.getTrangThai() == YeuCauHoTro.TrangThai.moi) {
            yeuCauHoTro.setTrangThai(YeuCauHoTro.TrangThai.dang_xu_ly);
        }
        
        YeuCauHoTro updatedYeuCau = yeuCauHoTroRepository.save(yeuCauHoTro);
        return convertToDTO(updatedYeuCau);
    }
    
    @Transactional
    public void xoaYeuCau(Long id) {
        YeuCauHoTro yeuCauHoTro = yeuCauHoTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        
        yeuCauHoTroRepository.delete(yeuCauHoTro);
    }
    
    public Map<String, Long> thongKeYeuCauTheoTrangThai() {
        Map<String, Long> stats = new HashMap<>();
        
        for (YeuCauHoTro.TrangThai trangThai : YeuCauHoTro.TrangThai.values()) {
            Long count = yeuCauHoTroRepository.countByTrangThai(trangThai);
            stats.put(trangThai.name(), count);
        }
        
        // Tổng số
        Long total = stats.values().stream().mapToLong(l -> l).sum();
        stats.put("tong", total);
        
        return stats;
    }
    
    private YeuCauHoTroDTO convertToDTO(YeuCauHoTro entity) {
        YeuCauHoTroDTO dto = new YeuCauHoTroDTO();
        
        dto.setId(entity.getId());
        
        if (entity.getNguoiDung() != null) {
            dto.setIdNguoiDung(entity.getNguoiDung().getId());
            dto.setTenNguoiDung(entity.getNguoiDung().getHo() + " " + entity.getNguoiDung().getTen());
        }
        
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setTrangThai(entity.getTrangThai().name());
        dto.setMucDoUuTien(entity.getMucDoUuTien().name());
        
        if (entity.getNgayTao() != null) {
            dto.setNgayTao(entity.getNgayTao().format(formatter));
        }
        
        if (entity.getNgayCapNhat() != null) {
            dto.setNgayCapNhat(entity.getNgayCapNhat().format(formatter));
        }
        
        if (entity.getNguoiPhuTrach() != null) {
            dto.setIdNguoiPhuTrach(entity.getNguoiPhuTrach().getId());
            dto.setTenNguoiPhuTrach(entity.getNguoiPhuTrach().getHo() + " " + entity.getNguoiPhuTrach().getTen());
        }
        
        return dto;
    }
    
    private PhanHoiYeuCauDTO convertPhanHoiToDTO(PhanHoiYeuCau entity) {
        PhanHoiYeuCauDTO dto = new PhanHoiYeuCauDTO();
        
        dto.setId(entity.getId());
        dto.setIdYeuCau(entity.getYeuCauHoTro().getId());
        
        if (entity.getNguoiDung() != null) {
            dto.setIdNguoiDung(entity.getNguoiDung().getId());
            dto.setTenNguoiDung(entity.getNguoiDung().getHo() + " " + entity.getNguoiDung().getTen());
            dto.setVaiTro(entity.getNguoiDung().getVaiTro().name());
        }
        
        dto.setNoiDung(entity.getNoiDung());
        
        if (entity.getNgayTao() != null) {
            dto.setNgayTao(entity.getNgayTao().format(formatter));
        }
        
        return dto;
    }
}
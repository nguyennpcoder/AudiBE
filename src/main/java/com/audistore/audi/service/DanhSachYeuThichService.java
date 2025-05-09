package com.audistore.audi.service;

import com.audistore.audi.dto.DanhSachYeuThichDTO;
import com.audistore.audi.model.DanhSachYeuThich;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.DanhSachYeuThichRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NguoiDungRepository;
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
import java.util.Map;

@Service
public class DanhSachYeuThichService {

    @Autowired
    private DanhSachYeuThichRepository danhSachYeuThichRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private MauXeRepository mauXeRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public DanhSachYeuThichDTO themVaoDanhSachYeuThich(Long idNguoiDung, Long idMauXe) {
        // Kiểm tra nếu đã tồn tại trong danh sách yêu thích
        if (danhSachYeuThichRepository.existsByNguoiDungIdAndMauXeId(idNguoiDung, idMauXe)) {
            throw new RuntimeException("Mẫu xe này đã có trong danh sách yêu thích");
        }
        
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiDung));
        
        MauXe mauXe = mauXeRepository.findById(idMauXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + idMauXe));
        
        DanhSachYeuThich yeuThich = new DanhSachYeuThich();
        yeuThich.setNguoiDung(nguoiDung);
        yeuThich.setMauXe(mauXe);
        
        DanhSachYeuThich savedYeuThich = danhSachYeuThichRepository.save(yeuThich);
        return convertToDTO(savedYeuThich);
    }
    
    public Page<DanhSachYeuThichDTO> getDanhSachYeuThichByNguoiDung(Long idNguoiDung, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("ngayThem").descending());
        Page<DanhSachYeuThich> yeuThichPage = danhSachYeuThichRepository.findByNguoiDungId(idNguoiDung, pageable);
        return yeuThichPage.map(this::convertToDTO);
    }
    
    public boolean kiemTraMauXeTrongDanhSachYeuThich(Long idNguoiDung, Long idMauXe) {
        return danhSachYeuThichRepository.existsByNguoiDungIdAndMauXeId(idNguoiDung, idMauXe);
    }
    
    @Transactional
    public void xoaKhoiDanhSachYeuThich(Long idNguoiDung, Long idMauXe) {
        if (!danhSachYeuThichRepository.existsByNguoiDungIdAndMauXeId(idNguoiDung, idMauXe)) {
            throw new RuntimeException("Mẫu xe không có trong danh sách yêu thích");
        }
        
        danhSachYeuThichRepository.deleteByNguoiDungIdAndMauXeId(idNguoiDung, idMauXe);
    }
    
    @Transactional
    public void xoaDanhSachYeuThich(Long id, Long idNguoiDung) {
        DanhSachYeuThich yeuThich = danhSachYeuThichRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục yêu thích với ID: " + id));
        
        // Chỉ người dùng sở hữu danh sách mới có thể xóa
        if (!yeuThich.getNguoiDung().getId().equals(idNguoiDung)) {
            throw new AccessDeniedException("Không có quyền xóa mục yêu thích này");
        }
        
        danhSachYeuThichRepository.delete(yeuThich);
    }
    
    public Map<String, Object> getThongKeDanhSachYeuThich(Long idNguoiDung) {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongSoXeYeuThich", danhSachYeuThichRepository.countByNguoiDungId(idNguoiDung));
        return thongKe;
    }
    
    private DanhSachYeuThichDTO convertToDTO(DanhSachYeuThich entity) {
        DanhSachYeuThichDTO dto = new DanhSachYeuThichDTO();
        
        dto.setId(entity.getId());
        dto.setIdNguoiDung(entity.getNguoiDung().getId());
        dto.setIdMauXe(entity.getMauXe().getId());
        dto.setTenMauXe(entity.getMauXe().getTenMau());
        
        // Lấy thông tin hình ảnh đại diện của mẫu xe (nếu có)
        if (!entity.getMauXe().getDanhSachHinhAnh().isEmpty()) {
            dto.setAnhDaiDien(entity.getMauXe().getDanhSachHinhAnh().get(0).getDuongDanAnh());
        }
        
        dto.setPhanLoai(entity.getMauXe().getDongXe().getPhanLoai().name());
        dto.setGiaCoban(entity.getMauXe().getGiaCoban().doubleValue());
        
        if (entity.getNgayThem() != null) {
            dto.setNgayThem(entity.getNgayThem().format(formatter));
        }
        
        return dto;
    }
}
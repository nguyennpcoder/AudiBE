package com.audistore.audi.service;

import com.audistore.audi.dto.PhanHoiYeuCauDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.model.PhanHoiYeuCau;
import com.audistore.audi.model.YeuCauHoTro;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.repository.PhanHoiYeuCauRepository;
import com.audistore.audi.repository.YeuCauHoTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhanHoiYeuCauService {

    @Autowired
    private PhanHoiYeuCauRepository phanHoiYeuCauRepository;

    @Autowired
    private YeuCauHoTroRepository yeuCauHoTroRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public PhanHoiYeuCauDTO taoPhanHoi(PhanHoiYeuCauDTO dto) {
        PhanHoiYeuCau phanHoi = new PhanHoiYeuCau();
        
        YeuCauHoTro yeuCauHoTro = yeuCauHoTroRepository.findById(dto.getIdYeuCau())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hỗ trợ với ID: " + dto.getIdYeuCau()));
        phanHoi.setYeuCauHoTro(yeuCauHoTro);
        
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        phanHoi.setNguoiDung(nguoiDung);
        
        phanHoi.setNoiDung(dto.getNoiDung());
        
        PhanHoiYeuCau savedPhanHoi = phanHoiYeuCauRepository.save(phanHoi);
        
        // Cập nhật trạng thái của yêu cầu hỗ trợ
        boolean isOwner = yeuCauHoTro.getNguoiDung().getId().equals(nguoiDung.getId());
        boolean isAssigned = yeuCauHoTro.getNguoiPhuTrach() != null && 
                            yeuCauHoTro.getNguoiPhuTrach().getId().equals(nguoiDung.getId());
        boolean isSupport = nguoiDung.getVaiTro() == NguoiDung.VaiTro.ho_tro;
        boolean isAdmin = nguoiDung.getVaiTro() == NguoiDung.VaiTro.quan_tri;
        
        // Nếu người phản hồi là người hỗ trợ được phân công hoặc admin,
        // và trạng thái hiện tại là "mới" thì cập nhật thành "đang xử lý"
        if ((isAssigned || isSupport || isAdmin) && yeuCauHoTro.getTrangThai() == YeuCauHoTro.TrangThai.moi) {
            yeuCauHoTro.setTrangThai(YeuCauHoTro.TrangThai.dang_xu_ly);
            yeuCauHoTroRepository.save(yeuCauHoTro);
        }
        
        // Nếu chưa có người phụ trách và người phản hồi là nhân viên hỗ trợ,
        // tự động phân công người phản hồi làm người phụ trách
        if (yeuCauHoTro.getNguoiPhuTrach() == null && (isSupport || isAdmin)) {
            yeuCauHoTro.setNguoiPhuTrach(nguoiDung);
            yeuCauHoTro.setTrangThai(YeuCauHoTro.TrangThai.dang_xu_ly);
            yeuCauHoTroRepository.save(yeuCauHoTro);
        }
        
        return convertToDTO(savedPhanHoi);
    }
    
    public List<PhanHoiYeuCauDTO> getPhanHoiByYeuCauId(Long idYeuCau) {
        List<PhanHoiYeuCau> phanHoiList = phanHoiYeuCauRepository.findByYeuCauHoTroIdOrderByNgayTaoAsc(idYeuCau);
        return phanHoiList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PhanHoiYeuCauDTO capNhatPhanHoi(Long id, PhanHoiYeuCauDTO dto, Long idNguoiCapNhat) {
        PhanHoiYeuCau phanHoi = phanHoiYeuCauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi với ID: " + id));
        
        // Kiểm tra quyền: chỉ người tạo phản hồi hoặc admin mới được cập nhật
        if (!phanHoi.getNguoiDung().getId().equals(idNguoiCapNhat)) {
            NguoiDung nguoiCapNhat = nguoiDungRepository.findById(idNguoiCapNhat)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiCapNhat));
            
            if (nguoiCapNhat.getVaiTro() != NguoiDung.VaiTro.quan_tri) {
                throw new AccessDeniedException("Không có quyền cập nhật phản hồi này");
            }
        }
        
        phanHoi.setNoiDung(dto.getNoiDung());
        PhanHoiYeuCau updatedPhanHoi = phanHoiYeuCauRepository.save(phanHoi);
        
        return convertToDTO(updatedPhanHoi);
    }
    
    @Transactional
    public void xoaPhanHoi(Long id, Long idNguoiXoa) {
        PhanHoiYeuCau phanHoi = phanHoiYeuCauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi với ID: " + id));
        
        // Kiểm tra quyền: chỉ người tạo phản hồi hoặc admin mới được xóa
        if (!phanHoi.getNguoiDung().getId().equals(idNguoiXoa)) {
            NguoiDung nguoiXoa = nguoiDungRepository.findById(idNguoiXoa)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiXoa));
            
            if (nguoiXoa.getVaiTro() != NguoiDung.VaiTro.quan_tri) {
                throw new AccessDeniedException("Không có quyền xóa phản hồi này");
            }
        }
        
        phanHoiYeuCauRepository.delete(phanHoi);
    }
    
    private PhanHoiYeuCauDTO convertToDTO(PhanHoiYeuCau entity) {
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
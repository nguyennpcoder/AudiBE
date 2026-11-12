package com.audistore.audi.service;

import com.audistore.audi.dto.BinhLuanDTO;
import com.audistore.audi.model.BaiViet;
import com.audistore.audi.model.BinhLuan;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.BaiVietRepository;
import com.audistore.audi.repository.BinhLuanRepository;
import com.audistore.audi.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;

@Service
@Transactional
public class BinhLuanService {

    private final BinhLuanRepository binhLuanRepository;
    private final BaiVietRepository baiVietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public BinhLuanService(
            BinhLuanRepository binhLuanRepository,
            BaiVietRepository baiVietRepository,
            NguoiDungRepository nguoiDungRepository) {
        this.binhLuanRepository = binhLuanRepository;
        this.baiVietRepository = baiVietRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    public BinhLuanDTO themBinhLuan(BinhLuanDTO dto) {
        BinhLuan binhLuan = new BinhLuan();
        
        BaiViet baiViet = baiVietRepository.findById(dto.getIdBaiViet())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + dto.getIdBaiViet()));
        binhLuan.setBaiViet(baiViet);
        
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        binhLuan.setNguoiDung(nguoiDung);
        
        binhLuan.setNoiDung(dto.getNoiDung());
        binhLuan.setTrangThai(BinhLuan.TrangThai.hien_thi);
        
        if (dto.getIdBinhLuanCha() != null) {
            BinhLuan binhLuanCha = binhLuanRepository.findById(dto.getIdBinhLuanCha())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận cha với ID: " + dto.getIdBinhLuanCha()));
            binhLuan.setBinhLuanCha(binhLuanCha);
        }
        
        BinhLuan savedBinhLuan = binhLuanRepository.save(binhLuan);
        return mapToDTO(savedBinhLuan);
    }

    public Page<BinhLuanDTO> getBinhLuanByBaiViet(Long idBaiViet, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BinhLuan> binhLuanPage = binhLuanRepository.findByBaiVietIdAndTrangThaiHienThi(idBaiViet, pageable);
        
        return binhLuanPage.map(this::mapToDTOWithReplies);
    }

    public BinhLuanDTO getBinhLuanById(Long id) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + id));
        return mapToDTOWithReplies(binhLuan);
    }

    @Transactional
    public BinhLuanDTO capNhatBinhLuan(Long id, BinhLuanDTO dto, Long idNguoiCapNhat) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + id));
        
        // Kiểm tra quyền: chỉ người tạo bình luận hoặc admin mới được cập nhật
        if (!binhLuan.getNguoiDung().getId().equals(idNguoiCapNhat)) {
            NguoiDung nguoiCapNhat = nguoiDungRepository.findById(idNguoiCapNhat)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiCapNhat));
            
            if (!nguoiCapNhat.getVaiTro().equals(NguoiDung.VaiTro.quan_tri)) {
                throw new RuntimeException("Không có quyền cập nhật bình luận này");
            }
        }
        
        binhLuan.setNoiDung(dto.getNoiDung());
        BinhLuan updatedBinhLuan = binhLuanRepository.save(binhLuan);
        return mapToDTO(updatedBinhLuan);
    }

    @Transactional
    public void xoaBinhLuan(Long id, Long idNguoiXoa) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + id));
        
        // Kiểm tra quyền: chỉ người tạo bình luận hoặc admin mới được xóa
        if (!binhLuan.getNguoiDung().getId().equals(idNguoiXoa)) {
            NguoiDung nguoiXoa = nguoiDungRepository.findById(idNguoiXoa)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiXoa));
            
            if (!nguoiXoa.getVaiTro().equals(NguoiDung.VaiTro.quan_tri)) {
                throw new RuntimeException("Không có quyền xóa bình luận này");
            }
        }
        
        binhLuan.setTrangThai(BinhLuan.TrangThai.xoa);
        binhLuanRepository.save(binhLuan);
    }

    @Transactional
    public void thayDoiTrangThai(Long id, String trangThai, Long idNguoiThayDoi) {
        NguoiDung nguoiThayDoi = nguoiDungRepository.findById(idNguoiThayDoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiThayDoi));
        
        if (!nguoiThayDoi.getVaiTro().equals(NguoiDung.VaiTro.quan_tri)) {
            throw new RuntimeException("Chỉ admin mới có quyền thay đổi trạng thái bình luận");
        }
        
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + id));
        
        binhLuan.setTrangThai(BinhLuan.TrangThai.valueOf(trangThai));
        binhLuanRepository.save(binhLuan);
    }

    public Long demBinhLuanByBaiViet(Long idBaiViet) {
        return binhLuanRepository.countByBaiVietIdAndTrangThaiHienThi(idBaiViet);
    }

    @Transactional
    public Map<String, Object> likeBinhLuan(Long commentId, Long userId) {
        BinhLuan binhLuan = binhLuanRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Kiểm tra xem user đã like comment này chưa
        if (binhLuan.getNguoiDungThich().contains(userId)) {
            throw new RuntimeException("Bạn đã thích bình luận này rồi");
        }
        
        // Thêm user vào danh sách người thích
        binhLuan.getNguoiDungThich().add(userId);
        
        // Tăng số lượt thích
        binhLuan.setSoLuotThich(binhLuan.getSoLuotThich() + 1);
        
        BinhLuan savedBinhLuan = binhLuanRepository.save(binhLuan);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Đã thích bình luận thành công");
        result.put("soLuotThich", savedBinhLuan.getSoLuotThich());
        result.put("daThich", true);
        
        return result;
    }

    @Transactional
    public Map<String, Object> unlikeBinhLuan(Long commentId, Long userId) {
        BinhLuan binhLuan = binhLuanRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Kiểm tra xem user đã like comment này chưa
        if (!binhLuan.getNguoiDungThich().contains(userId)) {
            throw new RuntimeException("Bạn chưa thích bình luận này");
        }
        
        // Xóa user khỏi danh sách người thích
        binhLuan.getNguoiDungThich().remove(userId);
        
        // Giảm số lượt thích
        binhLuan.setSoLuotThich(Math.max(0, binhLuan.getSoLuotThich() - 1));
        
        BinhLuan savedBinhLuan = binhLuanRepository.save(binhLuan);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Đã bỏ thích bình luận thành công");
        result.put("soLuotThich", savedBinhLuan.getSoLuotThich());
        result.put("daThich", false);
        
        return result;
    }

    private BinhLuanDTO mapToDTO(BinhLuan binhLuan) {
        BinhLuanDTO dto = new BinhLuanDTO();
        dto.setId(binhLuan.getId());
        dto.setIdBaiViet(binhLuan.getBaiViet().getId());
        dto.setIdNguoiDung(binhLuan.getNguoiDung().getId());
        // Sửa lỗi: ghép họ và tên
        dto.setTenNguoiDung(binhLuan.getNguoiDung().getHo() + " " + binhLuan.getNguoiDung().getTen());
        dto.setAvatarNguoiDung(binhLuan.getNguoiDung().getAvatar());
        dto.setNoiDung(binhLuan.getNoiDung());
        dto.setNgayBinhLuan(binhLuan.getNgayBinhLuan());
        dto.setTrangThai(binhLuan.getTrangThai().name());
        dto.setSoLuotThich(binhLuan.getSoLuotThich());
        
        if (binhLuan.getBinhLuanCha() != null) {
            dto.setIdBinhLuanCha(binhLuan.getBinhLuanCha().getId());
        }
        
        return dto;
    }

    private BinhLuanDTO mapToDTOWithReplies(BinhLuan binhLuan) {
        BinhLuanDTO dto = mapToDTO(binhLuan);
        
        // Lấy bình luận con
        if (binhLuan.getBinhLuanCon() != null && !binhLuan.getBinhLuanCon().isEmpty()) {
            List<BinhLuanDTO> binhLuanConDTO = binhLuan.getBinhLuanCon().stream()
                    .filter(bc -> bc.getTrangThai() == BinhLuan.TrangThai.hien_thi)
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            dto.setBinhLuanCon(binhLuanConDTO);
            dto.setTongSoBinhLuanCon(binhLuanConDTO.size());
        }
        
        return dto;
    }

    public List<BinhLuanDTO> getAllBinhLuanByBaiViet(Long idBaiViet, Long currentUserId) {
        List<BinhLuan> allComments = binhLuanRepository.findAllByBaiVietIdAndTrangThaiHienThi(idBaiViet);
        
        // Tạo map để tổ chức comments theo hierarchy
        Map<Long, BinhLuanDTO> commentMap = new HashMap<>();
        List<BinhLuanDTO> rootComments = new ArrayList<>();
        
        // First pass: tạo DTO cho tất cả comments
        for (BinhLuan comment : allComments) {
            BinhLuanDTO dto = mapToDTO(comment);
            commentMap.put(comment.getId(), dto);
        }
        
        // Second pass: tổ chức theo hierarchy và kiểm tra like
        for (BinhLuan comment : allComments) {
            BinhLuanDTO dto = commentMap.get(comment.getId());
            
            // Kiểm tra xem user hiện tại đã like comment này chưa
            if (currentUserId != null && comment.getNguoiDungThich() != null) {
                dto.setDaThich(comment.getNguoiDungThich().contains(currentUserId));
            } else {
                dto.setDaThich(false);
            }
            
            if (comment.getBinhLuanCha() == null) {
                // Root comment
                rootComments.add(dto);
            } else {
                // Reply comment
                BinhLuanDTO parent = commentMap.get(comment.getBinhLuanCha().getId());
                if (parent != null) {
                    if (parent.getBinhLuanCon() == null) {
                        parent.setBinhLuanCon(new ArrayList<>());
                    }
                    parent.getBinhLuanCon().add(dto);
                }
            }
        }
        
        // Sắp xếp root comments theo thời gian (mới nhất lên đầu)
        rootComments.sort((a, b) -> b.getNgayBinhLuan().compareTo(a.getNgayBinhLuan()));
        
        // Sắp xếp replies trong mỗi comment theo thời gian (cũ nhất lên đầu - giống social media)
        for (BinhLuanDTO rootComment : rootComments) {
            if (rootComment.getBinhLuanCon() != null) {
                rootComment.getBinhLuanCon().sort((a, b) -> a.getNgayBinhLuan().compareTo(b.getNgayBinhLuan()));
            }
        }
        
        return rootComments;
    }
} 
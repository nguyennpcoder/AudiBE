package com.audistore.audi.service;

import com.audistore.audi.dto.BaiVietDTO;
import com.audistore.audi.model.BaiViet;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.BaiVietRepository;
import com.audistore.audi.repository.NguoiDungRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BaiVietService {

    private final BaiVietRepository baiVietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ObjectMapper objectMapper;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public BaiVietService(
            BaiVietRepository baiVietRepository,
            NguoiDungRepository nguoiDungRepository,
            ObjectMapper objectMapper) {
        this.baiVietRepository = baiVietRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.objectMapper = objectMapper;
    }

    public List<BaiVietDTO> getAllBaiViet() {
        return baiVietRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<BaiVietDTO> getPagedBaiViet(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return baiVietRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
    
    public Page<BaiVietDTO> getPublicBaiViet(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayDang").descending());
        return baiVietRepository.findByDaXuatBan(true, pageable)
                .map(this::mapToDTO);
    }

    public BaiVietDTO getBaiVietById(Long id) {
        return baiVietRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
    }
    
    public List<BaiVietDTO> getBaiVietByTacGia(Long idTacGia) {
        NguoiDung tacGia = nguoiDungRepository.findById(idTacGia)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + idTacGia));
        
        return baiVietRepository.findByTacGia(tacGia).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BaiVietDTO> getBaiVietByDanhMuc(String danhMuc) {
        BaiViet.DanhMuc danhMucEnum = BaiViet.DanhMuc.valueOf(danhMuc);
        
        return baiVietRepository.findByDanhMuc(danhMucEnum).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<BaiVietDTO> getPublicBaiVietByDanhMuc(String danhMuc, int page, int size) {
        BaiViet.DanhMuc danhMucEnum = BaiViet.DanhMuc.valueOf(danhMuc);
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayDang").descending());
        
        return baiVietRepository.findByDanhMucAndDaXuatBan(danhMucEnum, true, pageable)
                .map(this::mapToDTO);
    }
    
    public List<BaiVietDTO> searchBaiViet(String keyword) {
        return baiVietRepository.searchBaiViet(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<BaiVietDTO> searchPublicBaiViet(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayDang").descending());
        
        return baiVietRepository.searchBaiVietPublic(keyword, pageable)
                .map(this::mapToDTO);
    }
    
    public List<BaiVietDTO> getBaiVietByTag(String tag) {
        return baiVietRepository.findByTag(tag).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public BaiVietDTO createBaiViet(BaiVietDTO baiVietDTO) {
        BaiViet baiViet = mapToEntity(baiVietDTO);
        return mapToDTO(baiVietRepository.save(baiViet));
    }
    
    public BaiVietDTO updateBaiViet(Long id, BaiVietDTO baiVietDTO) {
        BaiViet existingBaiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
        
        // Cập nhật thông tin bài viết
        updateEntityFromDTO(existingBaiViet, baiVietDTO);
        
        return mapToDTO(baiVietRepository.save(existingBaiViet));
    }
    
    public BaiVietDTO updateTrangThaiBaiViet(Long id, Boolean daXuatBan) {
        BaiViet existingBaiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
        
        existingBaiViet.setDaXuatBan(daXuatBan);
        
        return mapToDTO(baiVietRepository.save(existingBaiViet));
    }
    
    public void deleteBaiViet(Long id) {
        baiVietRepository.deleteById(id);
    }
    
    private BaiViet mapToEntity(BaiVietDTO dto) {
        BaiViet baiViet = new BaiViet();
        
        if (dto.getId() != null) {
            baiViet.setId(dto.getId());
        }
        
        baiViet.setTieuDe(dto.getTieuDe());
        baiViet.setNoiDung(dto.getNoiDung());
        baiViet.setAnhDaiDien(dto.getAnhDaiDien());
        
        // Set tác giả
        NguoiDung tacGia = nguoiDungRepository.findById(dto.getIdTacGia())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + dto.getIdTacGia()));
        baiViet.setTacGia(tacGia);
        
        // Set danh mục
        if (dto.getDanhMuc() != null) {
            baiViet.setDanhMuc(BaiViet.DanhMuc.valueOf(dto.getDanhMuc()));
        }
        
        // Set trạng thái xuất bản
        if (dto.getDaXuatBan() != null) {
            baiViet.setDaXuatBan(dto.getDaXuatBan());
        }
        
        // Set thẻ gán
        if (dto.getTheGan() != null && !dto.getTheGan().isEmpty()) {
            try {
                baiViet.setTheGan(objectMapper.writeValueAsString(dto.getTheGan()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Lỗi khi chuyển đổi thẻ gán thành JSON", e);
            }
        }
        
        return baiViet;
    }
    
    private void updateEntityFromDTO(BaiViet baiViet, BaiVietDTO dto) {
        if (dto.getTieuDe() != null) {
            baiViet.setTieuDe(dto.getTieuDe());
        }
        
        if (dto.getNoiDung() != null) {
            baiViet.setNoiDung(dto.getNoiDung());
        }
        
        if (dto.getAnhDaiDien() != null) {
            baiViet.setAnhDaiDien(dto.getAnhDaiDien());
        }
        
        // Cập nhật tác giả nếu có thay đổi
        if (dto.getIdTacGia() != null && !dto.getIdTacGia().equals(baiViet.getTacGia().getId())) {
            NguoiDung tacGia = nguoiDungRepository.findById(dto.getIdTacGia())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + dto.getIdTacGia()));
            baiViet.setTacGia(tacGia);
        }
        
        // Cập nhật danh mục nếu có
        if (dto.getDanhMuc() != null) {
            baiViet.setDanhMuc(BaiViet.DanhMuc.valueOf(dto.getDanhMuc()));
        }
        
        // Cập nhật trạng thái xuất bản nếu có
        if (dto.getDaXuatBan() != null) {
            baiViet.setDaXuatBan(dto.getDaXuatBan());
        }
        
        // Cập nhật thẻ gán nếu có
        if (dto.getTheGan() != null) {
            try {
                baiViet.setTheGan(objectMapper.writeValueAsString(dto.getTheGan()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Lỗi khi chuyển đổi thẻ gán thành JSON", e);
            }
        }
    }
    
    private BaiVietDTO mapToDTO(BaiViet baiViet) {
        BaiVietDTO dto = new BaiVietDTO();
        
        dto.setId(baiViet.getId());
        dto.setTieuDe(baiViet.getTieuDe());
        dto.setNoiDung(baiViet.getNoiDung());
        dto.setAnhDaiDien(baiViet.getAnhDaiDien());
        
        // Thông tin tác giả
        if (baiViet.getTacGia() != null) {
            dto.setIdTacGia(baiViet.getTacGia().getId());
            dto.setTenTacGia(baiViet.getTacGia().getHo() + " " + baiViet.getTacGia().getTen());
        }
        
        // Thông tin ngày đăng
        if (baiViet.getNgayDang() != null) {
            dto.setNgayDang(baiViet.getNgayDang().format(DATETIME_FORMATTER));
        }
        
        dto.setDaXuatBan(baiViet.getDaXuatBan());
        
        // Chuyển đổi danh mục
        if (baiViet.getDanhMuc() != null) {
            dto.setDanhMuc(baiViet.getDanhMuc().name());
        }
        
        // Chuyển đổi thẻ gán từ JSON string sang List<String>
        if (baiViet.getTheGan() != null && !baiViet.getTheGan().isEmpty()) {
            try {
                dto.setTheGan(Arrays.asList(objectMapper.readValue(baiViet.getTheGan(), String[].class)));
            } catch (JsonProcessingException e) {
                // Xử lý lỗi đơn giản bằng cách tạo một list trống
                dto.setTheGan(new ArrayList<>());
            }
        }
        
        return dto;
    }
}
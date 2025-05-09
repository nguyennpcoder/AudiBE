package com.audistore.audi.service;

import com.audistore.audi.dto.*;
import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.MauSac;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.TonKho;
import com.audistore.audi.model.TuyChon;
import com.audistore.audi.repository.DaiLyRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.TonKhoRepository;
import com.audistore.audi.service.specification.SearchSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private MauXeRepository mauXeRepository;
    
    @Autowired
    private DaiLyRepository daiLyRepository;
    
    @Autowired
    private TonKhoRepository tonKhoRepository;
    
    @Autowired
    private MauXeService mauXeService;
    
    @Autowired
    private DaiLyService daiLyService;
    
    @Autowired
    private TonKhoService tonKhoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SearchResultDTO<MauXeDTO> searchMauXe(SearchCriteriaDTO criteria) {
        // Tạo pageable
        Sort sort = getSortFromCriteria(criteria, "tenMau");
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        
        // Tạo specification
        Specification<MauXe> spec = Specification
                .where(SearchSpecifications.withKeyword(criteria.getKeyword()))
                .and(SearchSpecifications.inDongXeList(criteria.getDongXeIds()))
                .and(SearchSpecifications.inPhanLoaiList(criteria.getPhanLoai()))
                .and(SearchSpecifications.betweenNamSanXuat(criteria.getNamSanXuatTu(), criteria.getNamSanXuatDen()))
                .and(SearchSpecifications.betweenGia(criteria.getGiaTu(), criteria.getGiaDen()))
                .and(SearchSpecifications.withConHang(criteria.getConHang()));
        
        // Thực hiện tìm kiếm
        Page<MauXe> pageResult = mauXeRepository.findAll(spec, pageable);
        
        // Chuyển đổi kết quả với mapper riêng
        Page<MauXeDTO> dtoPage = pageResult.map(this::mauXeToDTO);
        
        return createSearchResult(dtoPage);
    }
    
    public SearchResultDTO<DaiLyDTO> searchDaiLy(SearchCriteriaDTO criteria) {
        // Tạo pageable
        Sort sort = getSortFromCriteria(criteria, "ten");
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        
        // Tạo specification
        Specification<DaiLy> spec = Specification
                .where(SearchSpecifications.withDaiLyKeyword(criteria.getKeyword()))
                .and(SearchSpecifications.inThanhPhoList(criteria.getThanhPho()))
                .and(SearchSpecifications.inTinhList(criteria.getTinh()))
                .and(SearchSpecifications.withLaTrungTamDichVu(criteria.getLaTrungTamDichVu()));
        
        // Thực hiện tìm kiếm
        Page<DaiLy> pageResult = daiLyRepository.findAll(spec, pageable);
        
        // Chuyển đổi kết quả với mapper riêng
        Page<DaiLyDTO> dtoPage = pageResult.map(this::daiLyToDTO);
        
        return createSearchResult(dtoPage);
    }
    
    public SearchResultDTO<TonKhoDTO> searchTonKho(SearchCriteriaDTO criteria) {
        // Tạo pageable
        Sort sort = getSortFromCriteria(criteria, "id");
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        
        // Tạo specification
        Specification<TonKho> spec = Specification
                .where(SearchSpecifications.withTonKhoKeyword(criteria.getKeyword()))
                .and(SearchSpecifications.inTrangThaiList(criteria.getTrangThai()))
                .and(SearchSpecifications.inMauSacList(criteria.getMauSacIds()))
                .and(SearchSpecifications.inDaiLyList(criteria.getDaiLyIds()))
                .and(SearchSpecifications.inMauXeList(criteria.getMauXeIds()));
        
        // Thực hiện tìm kiếm
        Page<TonKho> pageResult = tonKhoRepository.findAll(spec, pageable);
        
        // Chuyển đổi kết quả với mapper riêng
        Page<TonKhoDTO> dtoPage = pageResult.map(this::tonKhoToDTO);
        
        return createSearchResult(dtoPage);
    }
    
    // Các phương thức mapper riêng
    private MauXeDTO mauXeToDTO(MauXe mauXe) {
        MauXeDTO dto = new MauXeDTO();
        dto.setId(mauXe.getId());
        dto.setIdDong(mauXe.getDongXe().getId());
        dto.setTenDong(mauXe.getDongXe().getTen());
        dto.setTenMau(mauXe.getTenMau());
        dto.setNamSanXuat(mauXe.getNamSanXuat());
        dto.setGiaCoban(mauXe.getGiaCoban());
        dto.setMoTa(mauXe.getMoTa());
        dto.setThongSoKyThuat(mauXe.getThongSoKyThuat());
        dto.setConHang(mauXe.getConHang());

        // Chuyển LocalDate thành String
        if (mauXe.getNgayRaMat() != null) {
            dto.setNgayRaMat(mauXe.getNgayRaMat().format(DATE_FORMATTER));
        }

        // Lấy danh sách ID màu sắc
        if (mauXe.getDanhSachMauSac() != null) {
            List<Long> idsMauSac = mauXe.getDanhSachMauSac().stream()
                    .map(MauSac::getId)
                    .collect(Collectors.toList());
            dto.setIdsMauSac(idsMauSac);
        }

        // Lấy danh sách ID tùy chọn
        if (mauXe.getDanhSachTuyChon() != null) {
            List<Long> idsTuyChon = mauXe.getDanhSachTuyChon().stream()
                    .map(TuyChon::getId)
                    .collect(Collectors.toList());
            dto.setIdsTuyChon(idsTuyChon);
        }

        return dto;
    }

    private DaiLyDTO daiLyToDTO(DaiLy daiLy) {
        DaiLyDTO dto = new DaiLyDTO();
        dto.setId(daiLy.getId());
        dto.setTen(daiLy.getTen());
        dto.setDiaChi(daiLy.getDiaChi());
        dto.setThanhPho(daiLy.getThanhPho());
        dto.setTinh(daiLy.getTinh());
        dto.setMaBuuDien(daiLy.getMaBuuDien());
        dto.setQuocGia(daiLy.getQuocGia());
        dto.setSoDienThoai(daiLy.getSoDienThoai());
        dto.setEmail(daiLy.getEmail());
        dto.setGioLamViec(daiLy.getGioLamViec());
        dto.setLaTrungTamDichVu(daiLy.getLaTrungTamDichVu());
        
        return dto;
    }

    private TonKhoDTO tonKhoToDTO(TonKho tonKho) {
        TonKhoDTO dto = new TonKhoDTO();
        dto.setId(tonKho.getId());
        
        if (tonKho.getMauXe() != null) {
            dto.setIdMau(tonKho.getMauXe().getId());
            dto.setTenMau(tonKho.getMauXe().getTenMau());
        }
        
        if (tonKho.getDaiLy() != null) {
            dto.setIdDaiLy(tonKho.getDaiLy().getId());
            dto.setTenDaiLy(tonKho.getDaiLy().getTen());
        }
        
        if (tonKho.getMauSac() != null) {
            dto.setIdMauSac(tonKho.getMauSac().getId());
            dto.setTenMauSac(tonKho.getMauSac().getTen());
        }
        
        dto.setSoKhung(tonKho.getSoKhung());
        dto.setTrangThai(tonKho.getTrangThai() != null ? tonKho.getTrangThai().name() : null);
        
        if (tonKho.getNgaySanXuat() != null) {
            dto.setNgaySanXuat(tonKho.getNgaySanXuat().format(DATE_FORMATTER));
        }
        
        if (tonKho.getNgayVeDaiLy() != null) {
            dto.setNgayVeDaiLy(tonKho.getNgayVeDaiLy().format(DATE_FORMATTER));
        }
        
        dto.setTinhNangThem(tonKho.getTinhNangThem());
        dto.setGiaCuoiCung(tonKho.getGiaCuoiCung());
        
        return dto;
    }
    
    private <T> SearchResultDTO<T> createSearchResult(Page<T> page) {
        SearchResultDTO<T> result = new SearchResultDTO<>();
        result.setContent(page.getContent());
        result.setPageNo(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());
        
        return result;
    }
    
    private Sort getSortFromCriteria(SearchCriteriaDTO criteria, String defaultSortBy) {
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : defaultSortBy;
        String sortDir = criteria.getSortDir() != null ? criteria.getSortDir() : "asc";
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        return Sort.by(direction, sortBy);
    }
}
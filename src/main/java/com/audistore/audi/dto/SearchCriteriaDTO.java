package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDTO {
    // Tiêu chí tìm kiếm chung
    private String keyword;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy;
    private String sortDir = "asc";
    
    // Tiêu chí tìm kiếm mẫu xe
    private List<Long> dongXeIds;
    private List<String> phanLoai;
    private Integer namSanXuatTu;
    private Integer namSanXuatDen;
    private BigDecimal giaTu;
    private BigDecimal giaDen;
    private Boolean conHang;
    
    // Tiêu chí tìm kiếm đại lý
    private List<String> thanhPho;
    private List<String> tinh;
    private Boolean laTrungTamDichVu;
    
    // Tiêu chí tìm kiếm tồn kho
    private List<String> trangThai;
    private List<Long> mauSacIds;
    private List<Long> daiLyIds;
    private List<Long> mauXeIds;
}
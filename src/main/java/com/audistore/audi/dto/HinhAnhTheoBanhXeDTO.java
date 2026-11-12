package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhTheoBanhXeDTO {
    private Long id;
    private Long idMau;
    private Long idBanhXe;
    private Long idMauSac;
    private String duongDanAnh;
    private String loaiHinh;
    private Integer viTri;
} 
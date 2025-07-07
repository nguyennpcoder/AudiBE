package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhTheoNoiThatDTO {
    private Long id;
    private Long idMau;
    private Long idNoiThat;
    private String tenNoiThat;
    private String duongDanAnh;
    private String loaiHinh;
    private Integer viTri;
    private Long idMauSac;
}
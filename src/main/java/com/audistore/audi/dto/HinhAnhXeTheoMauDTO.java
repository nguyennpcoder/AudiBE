// backend/audi/src/main/java/com/audistore/audi/dto/HinhAnhXeTheoMauDTO.java
package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhXeTheoMauDTO {
    private Long id;
    private Long idMauXe;
    private Long idMauSac;
    private String tenMauSac;
    private String maHex;
    private String duongDanAnh;
    private String loaiHinh;
    private Integer viTri;
}
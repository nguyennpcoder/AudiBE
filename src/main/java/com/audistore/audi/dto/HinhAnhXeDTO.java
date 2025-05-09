package com.audistore.audi.dto;

import com.audistore.audi.model.HinhAnhXe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhXeDTO {
    private Long id;
    private Long idMauXe;
    private String duongDanAnh;
    private String loaiHinh;
    private Integer viTri;
    private String tenMauXe;  // Thông tin bổ sung
}
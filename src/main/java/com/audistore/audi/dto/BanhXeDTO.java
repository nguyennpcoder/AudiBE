package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanhXeDTO {
    private Long id;
    private String ten;
    private String moTa;
    private String kichThuoc;
    private String chatLieu;
    private BigDecimal giaThem;
    private String duongDanAnh;
} 
package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoiThatDTO {
    private Long id;
    private String ten;
    private String moTa;
    private String duongDanAnh;
    private BigDecimal giaThem;
    private Boolean laMacDinh;
}
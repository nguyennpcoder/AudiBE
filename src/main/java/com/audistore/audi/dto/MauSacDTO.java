package com.audistore.audi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauSacDTO {
    private Long id;

    @NotBlank(message = "Tên màu không được để trống")
    private String ten;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Mã màu phải có định dạng hex (vd: #FFFFFF)")
    private String maHex;

    private Boolean laMetallic;
    private String duongDanAnh;
    private BigDecimal giaThem;
}
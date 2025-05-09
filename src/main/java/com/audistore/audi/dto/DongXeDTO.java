package com.audistore.audi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DongXeDTO {
    private Long id;

    @NotBlank(message = "Tên dòng xe không được để trống")
    @Size(max = 50, message = "Tên dòng xe không được vượt quá 50 ký tự")
    private String ten;

    private String moTa;

    @NotNull(message = "Phân loại không được để trống")
    private String phanLoai;

    private String duongDanAnh;
}

package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXeDTO {
    private Long id;

    @NotNull(message = "ID dòng xe không được để trống")
    private Long idDong;

    private String tenDong;

    @NotBlank(message = "Tên mẫu xe không được để trống")
    @Size(max = 100, message = "Tên mẫu xe không được vượt quá 100 ký tự")
    private String tenMau;

    @NotNull(message = "Năm sản xuất không được để trống")
    @Min(value = 2000, message = "Năm sản xuất phải từ 2000 trở đi")
    private Integer namSanXuat;

    @NotNull(message = "Giá cơ bản không được để trống")
    @Min(value = 0, message = "Giá cơ bản phải lớn hơn hoặc bằng 0")
    private BigDecimal giaCoban;

    private String moTa;
    private String thongSoKyThuat;
    private Boolean conHang = true;
    private String ngayRaMat;

    private List<Long> idsMauSac = new ArrayList<>();
    private List<Long> idsTuyChon = new ArrayList<>();
}
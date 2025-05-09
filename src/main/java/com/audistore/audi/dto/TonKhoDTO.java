package com.audistore.audi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonKhoDTO {
    private Long id;
    
    @NotNull(message = "ID mẫu xe không được để trống")
    private Long idMau;
    private String tenMau;
    
    @NotNull(message = "ID đại lý không được để trống")
    private Long idDaiLy;
    private String tenDaiLy;
    
    @NotNull(message = "ID màu sắc không được để trống")
    private Long idMauSac;
    private String tenMauSac;
    
    @Size(min = 17, max = 17, message = "Số khung phải có đúng 17 ký tự")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Số khung không hợp lệ")
    private String soKhung;
    
    private String trangThai = "co_san";
    
    private String ngaySanXuat;
    private String ngayVeDaiLy;
    
    private String tinhNangThem;
    private BigDecimal giaCuoiCung;
}
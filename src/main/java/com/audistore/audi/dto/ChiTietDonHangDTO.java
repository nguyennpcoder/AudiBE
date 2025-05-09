package com.audistore.audi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangDTO {
    private Long id;
    
    @NotNull(message = "ID đơn hàng không được để trống")
    private Long idDonHang;
    
    @NotNull(message = "ID mẫu xe không được để trống")
    private Long idMauXe;
    private String tenMauXe;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer soLuong;
    
    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán phải lớn hơn hoặc bằng 0")
    private BigDecimal giaBan;
    
    private String mauSac;
    private String noiThat;
    private String options;
}
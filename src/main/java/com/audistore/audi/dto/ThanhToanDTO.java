package com.audistore.audi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanDTO {
    private Long id;

    @NotNull(message = "ID đơn hàng không được để trống")
    private Long idDonHang;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 0, message = "Số tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal soTien;

    @NotNull(message = "Loại thanh toán không được để trống")
    private String loaiThanhToan;

    private String ngayThanhToan;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThuc;

    private String maGiaoDich;
    private String trangThai;
}
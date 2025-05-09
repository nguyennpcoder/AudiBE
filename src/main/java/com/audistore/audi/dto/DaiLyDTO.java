package com.audistore.audi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DaiLyDTO {
    private Long id;
    
    @NotBlank(message = "Tên đại lý không được để trống")
    private String ten;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;
    
    @NotBlank(message = "Thành phố không được để trống")
    private String thanhPho;
    
    private String tinh;
    
    @NotBlank(message = "Mã bưu điện không được để trống")
    private String maBuuDien;
    
    @NotBlank(message = "Quốc gia không được để trống")
    private String quocGia;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String gioLamViec;
    
    private String viTriDiaLy;
    
    private Boolean laTrungTamDichVu = true;
    
    // Thông tin thống kê
    private Long soLuongXeTonKho;
}

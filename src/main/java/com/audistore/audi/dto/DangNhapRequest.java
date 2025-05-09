package com.audistore.audi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DangNhapRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap; // Có thể là email hoặc số điện thoại
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;
}
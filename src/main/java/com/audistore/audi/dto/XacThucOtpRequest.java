package com.audistore.audi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class XacThucOtpRequest {
    @NotBlank(message = "Số điện thoại không được để trống")
    private String soDienThoai;
    
    @NotBlank(message = "Mã OTP không được để trống")
    private String otp;
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String matKhauMoi;
}
package com.audistore.audi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetMatKhauRequest {
    @NotBlank(message = "Token không được để trống")
    private String token;
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String matKhauMoi;
}
package com.audistore.audi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuenMatKhauRequest {
    @NotBlank(message = "Email hoặc số điện thoại không được để trống")
    private String emailOrPhone;
    
    private String method; // "email" hoặc "sms"
}
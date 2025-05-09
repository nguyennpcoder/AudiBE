package com.audistore.audi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangKyNhanTinDTO {
    private Long id;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String hoTen;
    
    private List<String> quanTam;
    
    private Boolean dangHoatDong = true;
    
    private LocalDateTime ngayDangKy;
    
    private Boolean resubscribed = false;
}
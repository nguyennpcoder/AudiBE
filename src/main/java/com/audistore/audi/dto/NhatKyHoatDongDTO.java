package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhatKyHoatDongDTO {
    private Long id;
    private Long idNguoiDung;
    private String tenNguoiDung;
    private String emailNguoiDung;
    private String loaiHoatDong;
    private Map<String, Object> chiTietHoatDong;
    private String diaChiIp;
    private String thietBi;
    private LocalDateTime ngayTao;
}
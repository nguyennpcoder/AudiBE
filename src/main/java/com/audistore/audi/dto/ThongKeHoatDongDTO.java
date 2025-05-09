package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeHoatDongDTO {
    private Long tongSoHoatDong;
    private List<Map<String, Object>> thongKeTheoLoai;
    private List<Map<String, Object>> thongKeTheoNguoiDung;
    private List<Map<String, Object>> thongKeTheoNgay;
}
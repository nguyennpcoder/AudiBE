package com.audistore.audi.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ThongKeDangKyDTO {
    private int tongSoDangKy;
    private int soDangKyHoatDong;
    private int soDangKyDaXacThuc;
    private Map<String, Long> thongKeTheoQuanTam;
}
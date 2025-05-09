package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhSachYeuThichDTO {
    private Long id;
    private Long idNguoiDung;
    private Long idMauXe;
    private String tenMauXe;
    private String anhDaiDien;
    private String phanLoai;
    private Double giaCoban;
    private String ngayThem;
}
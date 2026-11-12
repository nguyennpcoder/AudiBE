package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinhLuanDTO {
    private Long id;
    private Long idBaiViet;
    private Long idNguoiDung;
    private String tenNguoiDung;
    private String avatarNguoiDung;
    private String noiDung;
    private LocalDateTime ngayBinhLuan;
    private String trangThai;
    private Long idBinhLuanCha;
    private List<BinhLuanDTO> binhLuanCon;
    private Integer soLuotThich;
    private Boolean daThich; // Thêm field này để kiểm tra user đã like chưa
    private Integer tongSoBinhLuanCon;
} 
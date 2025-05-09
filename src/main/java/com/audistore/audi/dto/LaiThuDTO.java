package com.audistore.audi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaiThuDTO {
    private Long id;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;

    @NotNull(message = "ID mẫu xe không được để trống")
    private Long idMau;
    private String tenMau;

    @NotNull(message = "ID đại lý không được để trống")
    private Long idDaiLy;
    private String tenDaiLy;

    @NotNull(message = "Thời gian hẹn không được để trống")
    @FutureOrPresent(message = "Thời gian hẹn phải là hiện tại hoặc tương lai")
    private LocalDateTime thoiGianHen;

    private String trangThai;
    private String ghiChu;
    private String ngayTao;
}
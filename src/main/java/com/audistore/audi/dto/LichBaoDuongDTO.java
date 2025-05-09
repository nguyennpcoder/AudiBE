package com.audistore.audi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichBaoDuongDTO {
    private Long id;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;

    @NotNull(message = "ID đại lý không được để trống")
    private Long idDaiLy;
    private String tenDaiLy;

    @NotBlank(message = "Số khung không được để trống")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Số khung VIN phải có 17 ký tự và không chứa I, O, Q")
    private String soKhung;

    @NotNull(message = "Ngày hẹn không được để trống")
    @FutureOrPresent(message = "Ngày hẹn phải là hiện tại hoặc tương lai")
    private LocalDateTime ngayHen;

    @NotNull(message = "Loại dịch vụ không được để trống")
    private String loaiDichVu;

    private String moTa;
    private String trangThai;
    private String ngayTao;
}

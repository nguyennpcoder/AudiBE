package com.audistore.audi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaDTO {
    private Long id;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;

    @NotNull(message = "ID mẫu xe không được để trống")
    private Long idMauXe;
    private String tenMauXe;

    @NotNull(message = "Số sao không được để trống")
    @Min(value = 1, message = "Số sao tối thiểu là 1")
    @Max(value = 5, message = "Số sao tối đa là 5")
    private Integer soSao;

    @Size(max = 100, message = "Tiêu đề không được vượt quá 100 ký tự")
    private String tieuDe;

    private String noiDung;
    private Boolean daMua;
    private String ngayTao;
    private String trangThai;
}
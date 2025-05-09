package com.audistore.audi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanHoiYeuCauDTO {
    private Long id;

    @NotNull(message = "ID yêu cầu hỗ trợ không được để trống")
    private Long idYeuCau;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;
    private String vaiTro;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    private String ngayTao;
}
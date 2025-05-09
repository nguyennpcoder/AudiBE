package com.audistore.audi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauHoTroDTO {
    private Long id;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String tieuDe;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    private String trangThai;
    private String mucDoUuTien;
    private String ngayTao;
    private String ngayCapNhat;

    private Long idNguoiPhuTrach;
    private String tenNguoiPhuTrach;

    private List<PhanHoiYeuCauDTO> danhSachPhanHoi;
}
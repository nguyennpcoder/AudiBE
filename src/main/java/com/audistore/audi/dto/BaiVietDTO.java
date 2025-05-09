package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaiVietDTO {
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String tieuDe;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    private String anhDaiDien;

    @NotNull(message = "ID tác giả không được để trống")
    private Long idTacGia;
    private String tenTacGia;

    private String ngayDang;
    private Boolean daXuatBan;

    @NotNull(message = "Danh mục không được để trống")
    private String danhMuc;

    private List<String> theGan;
}
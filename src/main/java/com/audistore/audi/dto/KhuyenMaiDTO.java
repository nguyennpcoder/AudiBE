package com.audistore.audi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDTO {
    private Long id;

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    private String ten;

    private String moTa;

    @NotNull(message = "Loại giảm giá không được để trống")
    private String loaiGiamGia;

    @NotNull(message = "Giá trị giảm không được để trống")
    @PositiveOrZero(message = "Giá trị giảm phải lớn hơn hoặc bằng 0")
    private BigDecimal giaTriGiam;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    private LocalDate ngayKetThuc;

    private String maKhuyenMai;

    @NotNull(message = "Loại áp dụng không được để trống")
    private String apDungCho;

    private BigDecimal giaTriToiThieu;

    private Integer gioiHanSuDung;

    private Integer soLanDaDung;

    private boolean conHieuLuc;

    private List<DieuKienKhuyenMaiDTO> danhSachDieuKien = new ArrayList<>();
}
package com.audistore.audi.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CauHinhTuyChinhDTO {
    private Long id;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;

    @NotNull(message = "ID mẫu xe không được để trống")
    private Long idMau;
    private String tenMau;

    @NotNull(message = "ID màu sắc không được để trống")
    private Long idMauSac;
    private String tenMauSac;

    // Thêm trường nội thất
    private Long idNoiThat;
    private String tenNoiThat;
    private BigDecimal giaNoiThat;

    // Thêm trường bánh xe
    private Long idBanhXe;
    private String tenBanhXe;
    private BigDecimal giaBanhXe;

    private String ten;

    @NotNull(message = "Tổng giá không được để trống")
    @Min(value = 0, message = "Tổng giá phải lớn hơn hoặc bằng 0")
    private BigDecimal tongGia;

    private String ngayTao;
    private String ngayCapNhat;

    private List<Long> danhSachIdTuyChon;
    private List<TuyChonDTO> danhSachTuyChon;
    
    // Thêm trường khuyến mãi
    private KhuyenMaiDTO khuyenMai;
    private BigDecimal giaSauKhuyenMai;
}
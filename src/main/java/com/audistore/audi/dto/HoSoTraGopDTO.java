package com.audistore.audi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoSoTraGopDTO {
    private Long id;
    
    @NotNull(message = "ID đơn hàng không được để trống")
    private Long idDonHang;
    
    private String nguoiMuaHo;
    private String soDienThoaiNguoiMuaHo;
    private String emailNguoiMuaHo;
    private String diaChiNguoiMuaHo;
    
    @Size(max = 100, message = "Tên ngân hàng đối tác không được vượt quá 100 ký tự")
    private String nganHangDoiTac;
    
    @NotNull(message = "Số tiền vay không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền vay phải lớn hơn 0")
    private BigDecimal soTienVay;
    
    @NotNull(message = "Lãi suất không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lãi suất phải lớn hơn 0")
    private BigDecimal laiSuat;
    
    @NotNull(message = "Kỳ hạn tháng không được để trống")
    @Min(value = 1, message = "Kỳ hạn tháng phải lớn hơn 0")
    private Integer kyHanThang;
    
    @NotNull(message = "Trả hàng tháng không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Trả hàng tháng phải lớn hơn 0")
    private BigDecimal traHangThang;
    
    private String trangThai = "dang_xu_ly"; // Mặc định là đang xử lý
    
    private LocalDateTime ngayNopHoSo;
    private LocalDateTime ngayQuyetDinh;
    
    // Thông tin bổ sung
    private String ghiChu;
    private String lyDoTuChoi;
    
    // Thông tin thêm để hiển thị
    private String tenKhachHang;
    private String emailKhachHang;
    private String soDienThoaiKhachHang;
    private String tenMauXe;
    private BigDecimal tongGiaTriDonHang;
}
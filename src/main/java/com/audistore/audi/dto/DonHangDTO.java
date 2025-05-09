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
public class DonHangDTO {
    private Long id;

    @NotNull(message = "ID người dùng không được để trống")
    private Long idNguoiDung;
    private String tenNguoiDung;

    private Long idTonKho;
    private Long idCauHinh;

    @NotNull(message = "ID đại lý không được để trống")
    private Long idDaiLy;
    private String tenDaiLy;

    private String ngayDat;
    private String trangThai;

    private BigDecimal tienDatCoc;

    @NotNull(message = "Tổng tiền không được để trống")
    @Min(value = 0, message = "Tổng tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal tongTien;

    private String ngayGiaoDuKien;
    private String ngayGiaoThucTe;
    private String phuongThucThanhToan;
    private String ghiChu;
    
    // Thêm trường khuyến mãi
    private Long idKhuyenMai;
    private String tenKhuyenMai;
    private BigDecimal tienGiam;
    
    // Chi tiết đơn hàng
    private List<ChiTietDonHangDTO> chiTietDonHangDTOs;

    public List<ChiTietDonHangDTO> getChiTietDonHangDTOs() {
        return chiTietDonHangDTOs;
    }

    public void setChiTietDonHangDTOs(List<ChiTietDonHangDTO> chiTietDonHangDTOs) {
        this.chiTietDonHangDTOs = chiTietDonHangDTOs;
    }
}
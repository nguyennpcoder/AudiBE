package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDashboardDTO {
    // Thống kê tổng quan
    private BigDecimal tongDoanhThu;
    private Long tongSoDonHang;
    private Long tongSoSanPham;
    private Long tongSoNguoiDung;
    
    // Thống kê theo sản phẩm
    private List<SanPhamThongKeDTO> sanPhamBanChay;
    private List<SanPhamThongKeDTO> sanPhamXemNhieu;
    
    // Thống kê theo thời gian
    private List<Map<String, Object>> doanhThuTheoThang;
    private List<Map<String, Object>> donHangTheoThang;
    private List<Map<String, Object>> luotXemTheoThang;
    
    // Tỷ lệ chuyển đổi đơn hàng
    private double tyLeChuyenDoi;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SanPhamThongKeDTO {
        private Long idMauXe;
        private String tenMauXe;
        private String anhDaiDien;
        private Long soLuong;
        private BigDecimal doanhThu;
        private Long luotXem;
    }
}
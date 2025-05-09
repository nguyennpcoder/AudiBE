package com.audistore.audi.repository;

import com.audistore.audi.model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface ThongKeRepository extends JpaRepository<DonHang, Long> {
    
    @Query(value = "SELECT YEAR(ngay_dat) as nam, MONTH(ngay_dat) as thang, COUNT(*) as soLuong, " +
           "SUM(tong_tien) as tongGiaTri FROM don_hang " +
           "WHERE trang_thai = 'da_giao' " +
           "GROUP BY YEAR(ngay_dat), MONTH(ngay_dat) " +
           "ORDER BY nam, thang", nativeQuery = true)
    List<Map<String, Object>> thongKeDoanhThuTheoThang();
    
    @Query(value = "SELECT YEAR(ngay_dat) as nam, MONTH(ngay_dat) as thang, COUNT(*) as soLuong " +
           "FROM don_hang " +
           "GROUP BY YEAR(ngay_dat), MONTH(ngay_dat) " +
           "ORDER BY nam, thang", nativeQuery = true)
    List<Map<String, Object>> thongKeDonHangTheoThang();
    
    @Query(value = "SELECT m.id_mau as idMauXe, m.ten_mau as tenMauXe, COUNT(d.id_don_hang) as soLuong, SUM(d.tong_tien) as doanhThu " +
           "FROM don_hang d " +
           "JOIN ton_kho t ON d.id_ton_kho = t.id_ton_kho " +
           "JOIN mau_xe m ON t.id_mau = m.id_mau " +
           "WHERE d.trang_thai = 'da_giao' " +
           "GROUP BY m.id_mau, m.ten_mau " +
           "ORDER BY soLuong DESC " +
           "LIMIT ?1", nativeQuery = true)
    List<Map<String, Object>> thongKeSanPhamBanChay(int limit);
    
    @Query(value = "SELECT COUNT(*) FROM don_hang WHERE trang_thai = 'da_giao'", nativeQuery = true)
    Long countDonHangThanhCong();
    
    @Query(value = "SELECT SUM(tong_tien) FROM don_hang WHERE trang_thai = 'da_giao'", nativeQuery = true)
    BigDecimal sumTongDoanhThu();
    
    @Query(value = "SELECT COUNT(DISTINCT id_mau) FROM mau_xe", nativeQuery = true)
    Long countTongSoSanPham();
    
    @Query(value = "SELECT COUNT(DISTINCT id_nguoi_dung) FROM nguoi_dung", nativeQuery = true)
    Long countTongSoNguoiDung();
}
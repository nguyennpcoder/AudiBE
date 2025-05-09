package com.audistore.audi.repository;

import com.audistore.audi.model.NhatKyHoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NhatKyXemSanPhamRepository extends JpaRepository<NhatKyHoatDong, Long> {
    
    // Sửa truy vấn - sử dụng SQL gốc thay vì JPQL
    @Query(value = "SELECT JSON_EXTRACT(chi_tiet_hoat_dong, '$.id_mau') as idMauXe, COUNT(*) as luotXem " +
           "FROM nhat_ky_hoat_dong " + 
           "WHERE loai_hoat_dong = 'xem_mau_xe' " +
           "GROUP BY JSON_EXTRACT(chi_tiet_hoat_dong, '$.id_mau') " +
           "ORDER BY luotXem DESC " +
           "LIMIT ?1", nativeQuery = true)
    List<Map<String, Object>> thongKeSanPhamXemNhieu(int limit);
    
    // Sửa truy vấn thống kê lượt xem theo tháng
    @Query(value = "SELECT YEAR(ngay_tao) as nam, MONTH(ngay_tao) as thang, COUNT(*) as soLuong " +
           "FROM nhat_ky_hoat_dong " +
           "WHERE loai_hoat_dong = 'xem_mau_xe' " +
           "GROUP BY YEAR(ngay_tao), MONTH(ngay_tao) " +
           "ORDER BY nam, thang", nativeQuery = true)
    List<Map<String, Object>> thongKeLuotXemTheoThang();
    
    // Sửa truy vấn đếm người dùng xem sản phẩm
    @Query(value = "SELECT COUNT(DISTINCT id_nguoi_dung) FROM nhat_ky_hoat_dong WHERE loai_hoat_dong = 'xem_mau_xe'", nativeQuery = true)
    Long countNguoiDungXemSanPham();
}
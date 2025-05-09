package com.audistore.audi.repository;

import com.audistore.audi.model.DonHang;
import com.audistore.audi.model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
    List<ThanhToan> findByDonHang(DonHang donHang);
    
    List<ThanhToan> findByTrangThai(ThanhToan.TrangThai trangThai);
    
    List<ThanhToan> findByLoaiThanhToan(ThanhToan.LoaiThanhToan loaiThanhToan);
    
    @Query("SELECT t FROM ThanhToan t WHERE t.ngayThanhToan BETWEEN :tuNgay AND :denNgay")
    List<ThanhToan> findByNgayThanhToanBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    @Query("SELECT t FROM ThanhToan t WHERE t.donHang.id = :idDonHang ORDER BY t.ngayThanhToan DESC")
    List<ThanhToan> findByIdDonHangOrderByNgayThanhToanDesc(Long idDonHang);
    
    @Query("SELECT t FROM ThanhToan t WHERE t.donHang.nguoiDung.id = :idNguoiDung ORDER BY t.ngayThanhToan DESC")
    List<ThanhToan> findByIdNguoiDungOrderByNgayThanhToanDesc(Long idNguoiDung);
    
    @Query("SELECT t FROM ThanhToan t WHERE t.phuongThuc = :phuongThuc")
    List<ThanhToan> findByPhuongThuc(String phuongThuc);
}
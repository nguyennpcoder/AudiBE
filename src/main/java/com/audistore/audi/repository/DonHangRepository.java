package com.audistore.audi.repository;

import com.audistore.audi.model.DonHang;
import com.audistore.audi.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    List<DonHang> findByNguoiDung(NguoiDung nguoiDung);
    
    List<DonHang> findByTrangThai(DonHang.TrangThai trangThai);
    
    @Query("SELECT d FROM DonHang d WHERE d.ngayDat BETWEEN :tuNgay AND :denNgay")
    List<DonHang> findByNgayDatBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.trangThai = :trangThai")
    Long countByTrangThai(DonHang.TrangThai trangThai);
    
    @Query("SELECT d FROM DonHang d WHERE d.nguoiDung.id = :idNguoiDung ORDER BY d.ngayDat DESC")
    List<DonHang> findByIdNguoiDungOrderByNgayDatDesc(Long idNguoiDung);
    
    @Query("SELECT d FROM DonHang d WHERE d.daiLy.id = :idDaiLy ORDER BY d.ngayDat DESC")
    List<DonHang> findByIdDaiLyOrderByNgayDatDesc(Long idDaiLy);
}
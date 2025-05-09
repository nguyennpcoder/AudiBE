package com.audistore.audi.repository;

import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.LichBaoDuong;
import com.audistore.audi.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LichBaoDuongRepository extends JpaRepository<LichBaoDuong, Long> {
    List<LichBaoDuong> findByNguoiDung(NguoiDung nguoiDung);
    
    List<LichBaoDuong> findByDaiLy(DaiLy daiLy);
    
    List<LichBaoDuong> findBySoKhungContainingIgnoreCase(String soKhung);
    
    List<LichBaoDuong> findByTrangThai(LichBaoDuong.TrangThai trangThai);
    
    List<LichBaoDuong> findByLoaiDichVu(LichBaoDuong.LoaiDichVu loaiDichVu);
    
    @Query("SELECT l FROM LichBaoDuong l WHERE l.ngayHen BETWEEN :tuNgay AND :denNgay")
    List<LichBaoDuong> findByNgayHenBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    @Query("SELECT l FROM LichBaoDuong l WHERE l.daiLy.id = :idDaiLy AND DATE(l.ngayHen) = DATE(:ngay)")
    List<LichBaoDuong> findByDaiLyAndNgay(Long idDaiLy, LocalDateTime ngay);
    
    @Query("SELECT l FROM LichBaoDuong l WHERE l.nguoiDung.id = :idNguoiDung ORDER BY l.ngayHen DESC")
    List<LichBaoDuong> findByIdNguoiDungOrderByNgayHenDesc(Long idNguoiDung);
    
    @Query("SELECT l FROM LichBaoDuong l WHERE l.daiLy.id = :idDaiLy ORDER BY l.ngayHen ASC")
    List<LichBaoDuong> findByIdDaiLyOrderByNgayHenAsc(Long idDaiLy);
}
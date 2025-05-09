package com.audistore.audi.repository;

import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.LaiThu;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LaiThuRepository extends JpaRepository<LaiThu, Long> {
    List<LaiThu> findByNguoiDung(NguoiDung nguoiDung);
    
    List<LaiThu> findByMauXe(MauXe mauXe);
    
    List<LaiThu> findByDaiLy(DaiLy daiLy);
    
    List<LaiThu> findByTrangThai(LaiThu.TrangThai trangThai);
    
    @Query("SELECT l FROM LaiThu l WHERE l.thoiGianHen BETWEEN :tuNgay AND :denNgay")
    List<LaiThu> findByThoiGianHenBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    @Query("SELECT l FROM LaiThu l WHERE l.daiLy.id = :idDaiLy AND DATE(l.thoiGianHen) = DATE(:ngay)")
    List<LaiThu> findByDaiLyAndNgay(Long idDaiLy, LocalDateTime ngay);
    
    @Query("SELECT l FROM LaiThu l WHERE l.nguoiDung.id = :idNguoiDung ORDER BY l.thoiGianHen DESC")
    List<LaiThu> findByIdNguoiDungOrderByThoiGianHenDesc(Long idNguoiDung);
    
    @Query("SELECT l FROM LaiThu l WHERE l.daiLy.id = :idDaiLy ORDER BY l.thoiGianHen ASC")
    List<LaiThu> findByIdDaiLyOrderByThoiGianHenAsc(Long idDaiLy);
}
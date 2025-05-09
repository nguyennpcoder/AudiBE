package com.audistore.audi.repository;

import com.audistore.audi.model.NhatKyHoatDong;
import com.audistore.audi.model.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NhatKyHoatDongRepository extends JpaRepository<NhatKyHoatDong, Long> {
    List<NhatKyHoatDong> findByNguoiDung(NguoiDung nguoiDung);
    
    Page<NhatKyHoatDong> findByNguoiDung(NguoiDung nguoiDung, Pageable pageable);
    
    List<NhatKyHoatDong> findByLoaiHoatDong(String loaiHoatDong);
    
    Page<NhatKyHoatDong> findByLoaiHoatDong(String loaiHoatDong, Pageable pageable);
    
    List<NhatKyHoatDong> findByNgayTaoBetween(LocalDateTime start, LocalDateTime end);
    
    Page<NhatKyHoatDong> findByNgayTaoBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT DISTINCT n.loaiHoatDong FROM NhatKyHoatDong n")
    List<String> findDistinctLoaiHoatDong();
    
    @Query("SELECT COUNT(n) FROM NhatKyHoatDong n WHERE n.loaiHoatDong = ?1")
    Long countByLoaiHoatDong(String loaiHoatDong);
    
    @Query("SELECT COUNT(n) FROM NhatKyHoatDong n WHERE n.nguoiDung.id = ?1")
    Long countByNguoiDungId(Long idNguoiDung);
}
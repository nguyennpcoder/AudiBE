package com.audistore.audi.repository;

import com.audistore.audi.model.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Long> {
    Optional<KhuyenMai> findByMaKhuyenMai(String maKhuyenMai);
    
    Page<KhuyenMai> findByNgayKetThucGreaterThanEqual(LocalDate date, Pageable pageable);
    
    @Query("SELECT k FROM KhuyenMai k WHERE k.ngayBatDau <= CURRENT_DATE AND k.ngayKetThuc >= CURRENT_DATE " +
           "AND (k.gioiHanSuDung IS NULL OR k.soLanDaDung < k.gioiHanSuDung)")
    Page<KhuyenMai> findAllActive(Pageable pageable);
    
    @Query("SELECT k FROM KhuyenMai k WHERE k.ngayBatDau <= CURRENT_DATE AND k.ngayKetThuc >= CURRENT_DATE " +
           "AND (k.gioiHanSuDung IS NULL OR k.soLanDaDung < k.gioiHanSuDung) " +
           "AND k.apDungCho = 'tat_ca_mau'")
    List<KhuyenMai> findActiveForAllModels();
    
    @Query("SELECT k FROM KhuyenMai k JOIN k.danhSachDieuKien d " +
           "WHERE k.ngayBatDau <= CURRENT_DATE AND k.ngayKetThuc >= CURRENT_DATE " +
           "AND (k.gioiHanSuDung IS NULL OR k.soLanDaDung < k.gioiHanSuDung) " +
           "AND k.apDungCho = 'mau_cu_the' AND d.loaiDoiTuong = 'mau_xe' AND d.idDoiTuong = :idMauXe")
    List<KhuyenMai> findActiveForModel(Long idMauXe);
    
    @Query("SELECT k FROM KhuyenMai k JOIN k.danhSachDieuKien d " +
           "WHERE k.ngayBatDau <= CURRENT_DATE AND k.ngayKetThuc >= CURRENT_DATE " +
           "AND (k.gioiHanSuDung IS NULL OR k.soLanDaDung < k.gioiHanSuDung) " +
           "AND k.apDungCho = 'dong_cu_the' AND d.loaiDoiTuong = 'dong_xe' AND d.idDoiTuong = :idDongXe")
    List<KhuyenMai> findActiveForSeries(Long idDongXe);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.DaiLy;
import com.audistore.audi.model.MauSac;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.TonKho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, Long>, JpaSpecificationExecutor<TonKho> {
    List<TonKho> findByMauXe(MauXe mauXe);
    List<TonKho> findByDaiLy(DaiLy daiLy);
    List<TonKho> findByMauSac(MauSac mauSac);
    List<TonKho> findByTrangThai(TonKho.TrangThai trangThai);
    Optional<TonKho> findBySoKhung(String soKhung);
    
    @Query("SELECT t FROM TonKho t WHERE t.mauXe.id = :mauXeId AND t.daiLy.id = :daiLyId AND t.trangThai = 'co_san'")
    List<TonKho> findAvailableByMauXeAndDaiLy(Long mauXeId, Long daiLyId);
    
    @Query("SELECT COUNT(t) FROM TonKho t WHERE t.mauXe.id = :mauXeId AND t.trangThai = 'co_san'")
    Integer countAvailableByMauXe(Long mauXeId);
    
    @Query("SELECT COUNT(t) FROM TonKho t WHERE t.mauXe.id = :mauXeId AND t.daiLy.id = :daiLyId AND t.trangThai = 'co_san'")
    Integer countAvailableByMauXeAndDaiLy(Long mauXeId, Long daiLyId);
    
    @Query("SELECT t FROM TonKho t WHERE t.mauXe.id = :mauXeId AND t.trangThai = 'co_san'")
    List<TonKho> findAvailableByMauXe(Long mauXeId);

    List<TonKho> findByMauXeId(Long idMauXe);
    List<TonKho> findByDaiLyId(Long idDaiLy);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.DanhGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {
    Page<DanhGia> findByMauXeIdAndTrangThai(Long mauXeId, DanhGia.TrangThai trangThai, Pageable pageable);
    
    @Query("SELECT d FROM DanhGia d WHERE d.mauXe.id = :mauXeId AND (d.trangThai = :trangThai OR (d.trangThai = 'cho_duyet' AND d.nguoiDung.id = :currentUserId))")
    Page<DanhGia> findByMauXeIdAndTrangThaiAndCurrentUserId(
        @Param("mauXeId") Long mauXeId, 
        @Param("trangThai") DanhGia.TrangThai trangThai,
        @Param("currentUserId") Long currentUserId,
        Pageable pageable);
    
    Page<DanhGia> findByNguoiDungId(Long nguoiDungId, Pageable pageable);
    Page<DanhGia> findByTrangThai(DanhGia.TrangThai trangThai, Pageable pageable);
    List<DanhGia> findByMauXeIdAndTrangThai(Long mauXeId, DanhGia.TrangThai trangThai);
}
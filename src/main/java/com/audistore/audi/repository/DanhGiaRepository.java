package com.audistore.audi.repository;

import com.audistore.audi.model.DanhGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {
    Page<DanhGia> findByMauXeId(Long idMauXe, Pageable pageable);
    Page<DanhGia> findByMauXeIdAndTrangThai(Long idMauXe, DanhGia.TrangThai trangThai, Pageable pageable);
    Page<DanhGia> findByNguoiDungId(Long idNguoiDung, Pageable pageable);
    Page<DanhGia> findByTrangThai(DanhGia.TrangThai trangThai, Pageable pageable);
    List<DanhGia> findByMauXeIdAndTrangThai(Long idMauXe, DanhGia.TrangThai trangThai);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.DanhSachYeuThich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DanhSachYeuThichRepository extends JpaRepository<DanhSachYeuThich, Long> {
    Page<DanhSachYeuThich> findByNguoiDungId(Long idNguoiDung, Pageable pageable);
    Optional<DanhSachYeuThich> findByNguoiDungIdAndMauXeId(Long idNguoiDung, Long idMauXe);
    boolean existsByNguoiDungIdAndMauXeId(Long idNguoiDung, Long idMauXe);
    void deleteByNguoiDungIdAndMauXeId(Long idNguoiDung, Long idMauXe);
    long countByNguoiDungId(Long idNguoiDung);
}
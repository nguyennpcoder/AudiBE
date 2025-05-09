package com.audistore.audi.repository;

import com.audistore.audi.model.YeuCauHoTro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YeuCauHoTroRepository extends JpaRepository<YeuCauHoTro, Long> {
    Page<YeuCauHoTro> findByNguoiDungId(Long idNguoiDung, Pageable pageable);
    Page<YeuCauHoTro> findByTrangThai(YeuCauHoTro.TrangThai trangThai, Pageable pageable);
    Page<YeuCauHoTro> findByNguoiPhuTrachId(Long idNguoiPhuTrach, Pageable pageable);
    Page<YeuCauHoTro> findByMucDoUuTien(YeuCauHoTro.MucDoUuTien mucDoUuTien, Pageable pageable);
    Long countByTrangThai(YeuCauHoTro.TrangThai trangThai);
    Page<YeuCauHoTro> findByTieuDeContainingIgnoreCase(String keyword, Pageable pageable);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.HoSoTraGop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoSoTraGopRepository extends JpaRepository<HoSoTraGop, Long> {
    List<HoSoTraGop> findByDonHangId(Long idDonHang);
    
    Page<HoSoTraGop> findByTrangThai(String trangThai, Pageable pageable);
    
    @Query("SELECT h FROM HoSoTraGop h JOIN h.donHang d JOIN d.nguoiDung n WHERE n.id = :idNguoiDung")
    List<HoSoTraGop> findByNguoiDungId(Long idNguoiDung);
    
    @Query("SELECT h FROM HoSoTraGop h JOIN h.donHang d JOIN d.nguoiDung n WHERE n.id = :idNguoiDung")
    Page<HoSoTraGop> findByNguoiDungId(Long idNguoiDung, Pageable pageable);
    
    @Query("SELECT count(h) FROM HoSoTraGop h WHERE h.trangThai = :trangThai")
    Long countByTrangThai(String trangThai);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.DieuKienKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DieuKienKhuyenMaiRepository extends JpaRepository<DieuKienKhuyenMai, Long> {
    List<DieuKienKhuyenMai> findByKhuyenMaiId(Long idKhuyenMai);
    
    void deleteByKhuyenMaiId(Long idKhuyenMai);
    
    List<DieuKienKhuyenMai> findByLoaiDoiTuongAndIdDoiTuong(
            DieuKienKhuyenMai.LoaiDoiTuong loaiDoiTuong, Long idDoiTuong);
}
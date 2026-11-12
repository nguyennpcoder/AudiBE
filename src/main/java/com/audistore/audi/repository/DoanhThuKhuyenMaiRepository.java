package com.audistore.audi.repository;

import com.audistore.audi.model.DoanhThuKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoanhThuKhuyenMaiRepository extends JpaRepository<DoanhThuKhuyenMai, Long> {
    Optional<DoanhThuKhuyenMai> findByIdKhuyenMai(Long idKhuyenMai);
}
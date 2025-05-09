package com.audistore.audi.repository;

import com.audistore.audi.model.DangKyNhanTin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DangKyNhanTinRepository extends JpaRepository<DangKyNhanTin, Long> {
    Optional<DangKyNhanTin> findByEmail(String email);
    List<DangKyNhanTin> findByDangHoatDong(Boolean dangHoatDong);
}
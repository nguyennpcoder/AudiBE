package com.audistore.audi.repository;

import com.audistore.audi.model.MatKhauNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatKhauNguoiDungRepository extends JpaRepository<MatKhauNguoiDung, Long> {
    Optional<MatKhauNguoiDung> findByIdNguoiDung(Long idNguoiDung);
}
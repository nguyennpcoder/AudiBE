package com.audistore.audi.repository;

import com.audistore.audi.model.DongXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DongXeRepository extends JpaRepository<DongXe, Long> {
    List<DongXe> findByPhanLoai(DongXe.PhanLoai phanLoai);
    List<DongXe> findByTenContainingIgnoreCase(String keyword);
    boolean existsByTen(String ten);
}
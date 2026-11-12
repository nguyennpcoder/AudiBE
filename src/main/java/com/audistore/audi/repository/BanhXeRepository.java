package com.audistore.audi.repository;

import com.audistore.audi.model.BanhXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanhXeRepository extends JpaRepository<BanhXe, Long> {
    List<BanhXe> findByTenContainingIgnoreCase(String keyword);
    List<BanhXe> findByKichThuoc(String kichThuoc);
    List<BanhXe> findByChatLieu(String chatLieu);
    boolean existsByTen(String ten);
} 
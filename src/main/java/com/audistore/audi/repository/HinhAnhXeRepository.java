package com.audistore.audi.repository;

import com.audistore.audi.model.HinhAnhXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhXeRepository extends JpaRepository<HinhAnhXe, Long> {
    List<HinhAnhXe> findByMauXeId(Long idMauXe);
    List<HinhAnhXe> findByMauXeIdAndLoaiHinh(Long idMauXe, HinhAnhXe.LoaiHinh loaiHinh);
    long countByMauXeId(Long idMauXe);
    void deleteByMauXeId(Long idMauXe);
}
// backend/audi/src/main/java/com/audistore/audi/repository/HinhAnhXeTheoMauRepository.java
package com.audistore.audi.repository;

import com.audistore.audi.model.HinhAnhXeTheoMau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhXeTheoMauRepository extends JpaRepository<HinhAnhXeTheoMau, Long> {
    List<HinhAnhXeTheoMau> findByMauXeId(Long idMauXe);
    List<HinhAnhXeTheoMau> findByMauXeIdAndMauSacId(Long idMauXe, Long idMauSac);
    List<HinhAnhXeTheoMau> findByMauXeIdAndMauSacIdAndLoaiHinh(Long idMauXe, Long idMauSac, String loaiHinh);
}
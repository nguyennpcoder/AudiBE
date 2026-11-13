package com.audistore.audi.repository;

import com.audistore.audi.model.HinhAnhXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhXeRepository extends JpaRepository<HinhAnhXe, Long> {
    List<HinhAnhXe> findByMauXeId(Long idMauXe);
    List<HinhAnhXe> findByMauXeIdAndLoaiHinh(Long idMauXe, HinhAnhXe.LoaiHinh loaiHinh);
    long countByMauXeId(Long idMauXe);
    void deleteByMauXeId(Long idMauXe);

    /**
     * Lightweight projection for the /models overview. Returns only the
     * columns needed to pick one cover image per model, without loading
     * full entities or triggering a query per row.
     */
    @Query("SELECT h.mauXe.id, h.duongDanAnh, h.loaiHinh, h.viTri FROM HinhAnhXe h")
    List<Object[]> findAnhDaiDienCandidates();
}
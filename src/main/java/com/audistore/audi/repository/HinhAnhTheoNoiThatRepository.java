package com.audistore.audi.repository;

import com.audistore.audi.model.HinhAnhTheoNoiThat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhTheoNoiThatRepository extends JpaRepository<HinhAnhTheoNoiThat, Long> {
    List<HinhAnhTheoNoiThat> findByMauXeIdAndNoiThatIdOrderByViTri(Long mauXeId, Long noiThatId);
    List<HinhAnhTheoNoiThat> findByMauXeIdAndNoiThatIdAndLoaiHinhOrderByViTri(Long mauXeId, Long noiThatId, HinhAnhTheoNoiThat.LoaiHinh loaiHinh);
    List<HinhAnhTheoNoiThat> findByMauXeIdAndNoiThatIdAndMauSacIdOrderByViTri(Long mauXeId, Long noiThatId, Long mauSacId);
}
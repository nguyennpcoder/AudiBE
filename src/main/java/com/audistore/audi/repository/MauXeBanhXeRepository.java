package com.audistore.audi.repository;

import com.audistore.audi.model.MauXeBanhXe;
import com.audistore.audi.model.MauXeBanhXeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MauXeBanhXeRepository extends JpaRepository<MauXeBanhXe, MauXeBanhXeId> {
    List<MauXeBanhXe> findByMauXeId(Long idMau);
    List<MauXeBanhXe> findByBanhXeId(Long idBanhXe);
    List<MauXeBanhXe> findByMauXeIdAndLaMacDinh(Long idMau, Boolean laMacDinh);
} 
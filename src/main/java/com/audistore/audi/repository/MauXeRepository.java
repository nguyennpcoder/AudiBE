package com.audistore.audi.repository;

import com.audistore.audi.model.DongXe;
import com.audistore.audi.model.MauXe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MauXeRepository extends JpaRepository<MauXe, Long>, JpaSpecificationExecutor<MauXe> {
    List<MauXe> findByDongXe(DongXe dongXe);
    List<MauXe> findByNamSanXuat(Integer namSanXuat);
    List<MauXe> findByConHang(Boolean conHang);
    List<MauXe> findByTenMauContainingIgnoreCase(String keyword);
    
    @Query("SELECT m FROM MauXe m WHERE m.giaCoban BETWEEN :giaTu AND :giaDen")
    List<MauXe> findByGiaTrongKhoang(BigDecimal giaTu, BigDecimal giaDen);
    
    @Query("SELECT m FROM MauXe m JOIN m.dongXe d WHERE d.phanLoai = :phanLoai")
    List<MauXe> findByPhanLoai(DongXe.PhanLoai phanLoai);
}
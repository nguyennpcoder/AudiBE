package com.audistore.audi.repository;

import com.audistore.audi.model.TuyChon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TuyChonRepository extends JpaRepository<TuyChon, Long> {
    
    List<TuyChon> findByDanhMuc(TuyChon.DanhMuc danhMuc);
    
    @Query("SELECT t FROM TuyChon t JOIN t.danhSachMauXe m WHERE m.id = :mauXeId")
    List<TuyChon> findByDanhSachMauXeId(@Param("mauXeId") Long mauXeId);
}
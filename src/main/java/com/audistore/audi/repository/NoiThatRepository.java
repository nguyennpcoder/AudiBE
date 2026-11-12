package com.audistore.audi.repository;

import com.audistore.audi.model.NoiThat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoiThatRepository extends JpaRepository<NoiThat, Long> {
    
    @Query("SELECT n FROM NoiThat n JOIN n.danhSachMauXe m WHERE m.id = :mauXeId")
    List<NoiThat> findByDanhSachMauXeId(@Param("mauXeId") Long mauXeId);
}
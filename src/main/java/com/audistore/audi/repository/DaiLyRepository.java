package com.audistore.audi.repository;

import com.audistore.audi.model.DaiLy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaiLyRepository extends JpaRepository<DaiLy, Long>, JpaSpecificationExecutor<DaiLy> {
    List<DaiLy> findByThanhPho(String thanhPho);
    List<DaiLy> findByTinh(String tinh);
    List<DaiLy> findByLaTrungTamDichVu(Boolean laTrungTamDichVu);
    
    @Query("SELECT d FROM DaiLy d WHERE LOWER(d.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.thanhPho) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.diaChi) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DaiLy> searchDaiLy(String keyword);
    
    @Query("SELECT d FROM DaiLy d WHERE LOWER(d.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.thanhPho) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.diaChi) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<DaiLy> timKiemDaiLy(String keyword, Pageable pageable);
    
    @Query(value = "SELECT d.* FROM dai_ly d " +
            "JOIN ton_kho tk ON d.id_dai_ly = tk.id_dai_ly " +
            "WHERE tk.id_mau = :mauXeId AND tk.trang_thai = 'co_san' " +
            "GROUP BY d.id_dai_ly", nativeQuery = true)
    List<DaiLy> findDaiLyCoMauXe(Long mauXeId);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.HinhAnhTheoBanhXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhTheoBanhXeRepository extends JpaRepository<HinhAnhTheoBanhXe, Long> {
    // Sử dụng các method đúng với cấu trúc model
    List<HinhAnhTheoBanhXe> findByMauXe_IdAndBanhXe_Id(Long idMau, Long idBanhXe);
    List<HinhAnhTheoBanhXe> findByMauXe_IdAndBanhXe_IdAndMauSac_Id(Long idMau, Long idBanhXe, Long idMauSac);
    List<HinhAnhTheoBanhXe> findByMauXe_IdAndBanhXe_IdAndLoaiHinh(Long idMau, Long idBanhXe, HinhAnhTheoBanhXe.LoaiHinh loaiHinh);
    void deleteByMauXe_IdAndBanhXe_Id(Long idMau, Long idBanhXe);
} 
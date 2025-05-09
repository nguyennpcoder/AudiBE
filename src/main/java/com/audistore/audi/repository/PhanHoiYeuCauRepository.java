package com.audistore.audi.repository;

import com.audistore.audi.model.PhanHoiYeuCau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhanHoiYeuCauRepository extends JpaRepository<PhanHoiYeuCau, Long> {
    List<PhanHoiYeuCau> findByYeuCauHoTroIdOrderByNgayTaoAsc(Long idYeuCau);
}
package com.audistore.audi.repository;

import com.audistore.audi.model.CauHinhTuyChiNh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauHinhTuyChinhRepository extends JpaRepository<CauHinhTuyChiNh, Long> {
    List<CauHinhTuyChiNh> findByNguoiDungId(Long idNguoiDung);
    List<CauHinhTuyChiNh> findByMauXeId(Long idMauXe);
}
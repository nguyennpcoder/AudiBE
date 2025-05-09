package com.audistore.audi.repository;

import com.audistore.audi.model.NhomQuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NhomQuyenRepository extends JpaRepository<NhomQuyen, Long> {
    Optional<NhomQuyen> findByTen(String ten);
    boolean existsByTen(String ten);
    
    @Query("SELECT nq FROM NhomQuyen nq JOIN nq.nguoiDung nd WHERE nd.id = :idNguoiDung")
    Set<NhomQuyen> findByNguoiDungId(Long idNguoiDung);
    
    @Query("SELECT DISTINCT q.maQuyen FROM NhomQuyen nq JOIN nq.danhSachQuyen q JOIN nq.nguoiDung nd WHERE nd.id = :idNguoiDung")
    Set<String> findQuyenByNguoiDungId(Long idNguoiDung);
}
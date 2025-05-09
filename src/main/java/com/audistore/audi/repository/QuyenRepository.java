package com.audistore.audi.repository;

import com.audistore.audi.model.Quyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface QuyenRepository extends JpaRepository<Quyen, Long> {
    Optional<Quyen> findByMaQuyen(String maQuyen);
    Optional<Quyen> findByTen(String ten);
    Set<Quyen> findByMaQuyenIn(Set<String> maQuyen);
    boolean existsByMaQuyen(String maQuyen);
    boolean existsByTen(String ten);
}
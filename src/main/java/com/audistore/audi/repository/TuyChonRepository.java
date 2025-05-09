package com.audistore.audi.repository;

import com.audistore.audi.model.TuyChon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuyChonRepository extends JpaRepository<TuyChon, Long> {
}
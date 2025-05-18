package com.audistore.audi.repository;

import com.audistore.audi.model.NoiThat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoiThatRepository extends JpaRepository<NoiThat, Long> {
}
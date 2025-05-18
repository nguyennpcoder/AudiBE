package com.audistore.audi.repository;

import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.MauXeNoiThat;
import com.audistore.audi.model.MauXeNoiThatId;
import com.audistore.audi.model.NoiThat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MauXeNoiThatRepository extends JpaRepository<MauXeNoiThat, MauXeNoiThatId> {
    List<MauXeNoiThat> findByMauXe(MauXe mauXe);

    Optional<MauXeNoiThat> findByMauXeAndLaMacDinh(MauXe mauXe, Boolean laMacDinh);

    @Query("SELECT mxn FROM MauXeNoiThat mxn WHERE mxn.mauXe.id = :idMau AND mxn.laMacDinh = true")
    Optional<MauXeNoiThat> findDefaultByMauXeId(Long idMau);
}
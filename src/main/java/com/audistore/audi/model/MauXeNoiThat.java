package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "mau_xe_noi_that")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXeNoiThat {

    @EmbeddedId
    private MauXeNoiThatId id;

    @ManyToOne
    @MapsId("idMau")
    @JoinColumn(name = "id_mau")
    private MauXe mauXe;

    @ManyToOne
    @MapsId("idNoiThat")
    @JoinColumn(name = "id_noi_that")
    private NoiThat noiThat;

    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh = false;
}
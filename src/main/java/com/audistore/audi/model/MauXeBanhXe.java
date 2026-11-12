package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "mau_xe_banh_xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXeBanhXe {

    @EmbeddedId
    private MauXeBanhXeId id;

    @ManyToOne
    @MapsId("idMau")
    @JoinColumn(name = "id_mau")
    private MauXe mauXe;

    @ManyToOne
    @MapsId("idBanhXe")
    @JoinColumn(name = "id_banh_xe")
    private BanhXe banhXe;

    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh = false;
} 
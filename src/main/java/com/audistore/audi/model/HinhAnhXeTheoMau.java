// backend/audi/src/main/java/com/audistore/audi/model/HinhAnhXeTheoMau.java
package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "hinh_anh_xe_theo_mau")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhXeTheoMau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh_mau")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac", nullable = false)
    private MauSac mauSac;

    @Column(name = "duong_dan_anh", nullable = false)
    private String duongDanAnh;

    @Column(name = "loai_hinh", nullable = false)
    private String loaiHinh;

    @Column(name = "vi_tri")
    private Integer viTri;
}
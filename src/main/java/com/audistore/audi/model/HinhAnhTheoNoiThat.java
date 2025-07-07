package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "hinh_anh_theo_noi_that")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhTheoNoiThat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh_noi_that")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;

    @ManyToOne
    @JoinColumn(name = "id_noi_that", nullable = false)
    private NoiThat noiThat;

    @Column(name = "duong_dan_anh", nullable = false)
    private String duongDanAnh;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hinh", nullable = false)
    private LoaiHinh loaiHinh;

    @Column(name = "vi_tri")
    private Integer viTri;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    private MauSac mauSac;

    public enum LoaiHinh {
        ngoai_that, noi_that, chi_tiet, tinh_nang, thu_nho
    }
}
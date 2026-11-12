package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "hinh_anh_theo_banh_xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhTheoBanhXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh_banh_xe")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;

    @ManyToOne
    @JoinColumn(name = "id_banh_xe", nullable = false)
    private BanhXe banhXe;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    private MauSac mauSac;

    @Column(name = "duong_dan_anh", nullable = false)
    private String duongDanAnh;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hinh", nullable = false)
    private LoaiHinh loaiHinh;

    @Column(name = "vi_tri")
    private Integer viTri;

    public enum LoaiHinh {
        ngoai_that, banh_xe, chi_tiet, tinh_nang, thu_nho
    }
} 
package com.audistore.audi.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mau_sac")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau_sac")
    private Long id;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "ma_hex")
    private String maHex;

    @Column(name = "la_metallic")
    private Boolean laMetallic = false;

    @Column(name = "duong_dan_anh")
    private String duongDanAnh;
    @Column(name = "duong_dan_anh_noi_that")
    private String duongDanAnhNoiThat;

    @Column(name = "gia_them", precision = 10, scale = 2)
    private BigDecimal giaThem = BigDecimal.ZERO;

    @ManyToMany(mappedBy = "danhSachMauSac")
    private List<MauXe> danhSachMauXe = new ArrayList<>();

    @OneToMany(mappedBy = "mauSac", cascade = CascadeType.ALL)
    private List<TonKho> danhSachTonKho = new ArrayList<>();

    @OneToMany(mappedBy = "mauSac", cascade = CascadeType.ALL)
    private List<CauHinhTuyChiNh> danhSachCauHinh = new ArrayList<>();
}
package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dong_xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DongXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dong")
    private Long id;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "phan_loai", nullable = false)
    private PhanLoai phanLoai;

    @Column(name = "duong_dan_anh")
    private String duongDanAnh;

    @OneToMany(mappedBy = "dongXe", cascade = CascadeType.ALL)
    private List<MauXe> danhSachMauXe = new ArrayList<>();

    public enum PhanLoai {
        SUV, Sedan, Coupe, Convertible, Sportback, Dien
    }
}

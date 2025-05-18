package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "noi_that")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoiThat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_noi_that")
    private Long id;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "duong_dan_anh")
    private String duongDanAnh;

    @Column(name = "gia_them", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaThem;

    @ManyToMany(mappedBy = "danhSachNoiThat")
    private List<MauXe> danhSachMauXe = new ArrayList<>();
}
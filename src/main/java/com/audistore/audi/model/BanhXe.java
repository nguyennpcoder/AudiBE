package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "banh_xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanhXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_banh_xe")
    private Long id;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "kich_thuoc")
    private String kichThuoc;

    @Column(name = "chat_lieu")
    private String chatLieu;

    @Column(name = "gia_them", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaThem;

    @Column(name = "duong_dan_anh")
    private String duongDanAnh;

    @ManyToMany(mappedBy = "danhSachBanhXe")
    private List<MauXe> danhSachMauXe = new ArrayList<>();
} 
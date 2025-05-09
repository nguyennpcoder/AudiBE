package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "hinh_anh_xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhXe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;
    
    @Column(name = "duong_dan_anh", nullable = false)
    private String duongDanAnh;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hinh", nullable = false)
    private LoaiHinh loaiHinh;
    
    @Column(name = "vi_tri")
    private Integer viTri;
    
    public enum LoaiHinh {
        ngoai_that, noi_that, chi_tiet, tinh_nang, thu_nho
    }
}
package com.audistore.audi.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tuy_chon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TuyChon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tuy_chon")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "danh_muc", nullable = false)
    private DanhMuc danhMuc;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "gia", nullable = false, precision = 10, scale = 2)
    private BigDecimal gia;

    @ManyToMany(mappedBy = "danhSachTuyChon")
    private List<MauXe> danhSachMauXe = new ArrayList<>();

    @ManyToMany(mappedBy = "danhSachTuyChon")
    private List<CauHinhTuyChiNh> danhSachCauHinh = new ArrayList<>();

    public enum DanhMuc {
        Noi_that, Ngoai_that, Hieu_suat, Cong_nghe, An_toan, Tien_nghi
    }
}
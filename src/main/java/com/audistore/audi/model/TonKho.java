package com.audistore.audi.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ton_kho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ton_kho")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;

    @ManyToOne
    @JoinColumn(name = "id_dai_ly", nullable = false)
    private DaiLy daiLy;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac", nullable = false)
    private MauSac mauSac;

    @Column(name = "so_khung", unique = true)
    private String soKhung;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.co_san;

    @Column(name = "ngay_san_xuat")
    private LocalDate ngaySanXuat;

    @Column(name = "ngay_ve_dai_ly")
    private LocalDate ngayVeDaiLy;

    @Column(name = "tinh_nang_them")
    private String tinhNangThem;

    @Column(name = "gia_cuoi_cung", precision = 12, scale = 2)
    private BigDecimal giaCuoiCung;

    @OneToMany(mappedBy = "tonKho", cascade = CascadeType.ALL)
    private List<DonHang> danhSachDonHang = new ArrayList<>();

    public enum TrangThai {
        co_san, da_dat, da_ban, dang_van_chuyen
    }
}
package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dai_ly")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DaiLy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dai_ly")
    private Long id;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "dia_chi", nullable = false)
    private String diaChi;

    @Column(name = "thanh_pho", nullable = false)
    private String thanhPho;

    @Column(name = "tinh")
    private String tinh;

    @Column(name = "ma_buu_dien", nullable = false)
    private String maBuuDien;

    @Column(name = "quoc_gia", nullable = false)
    private String quocGia;

    @Column(name = "so_dien_thoai", nullable = false)
    private String soDienThoai;

    @Column(name = "email")
    private String email;

    // Cách xử lý JSON trong Hibernate 6
    @Column(name = "gio_lam_viec", columnDefinition = "json")
    private String gioLamViec;

    @Column(name = "vi_tri_dia_ly", columnDefinition = "POINT")
    private String viTriDiaLy;

    @Column(name = "la_trung_tam_dich_vu")
    private Boolean laTrungTamDichVu = true;

    @OneToMany(mappedBy = "daiLy", cascade = CascadeType.ALL)
    private List<TonKho> danhSachTonKho = new ArrayList<>();

    @OneToMany(mappedBy = "daiLy", cascade = CascadeType.ALL)
    private List<LaiThu> danhSachLaiThu = new ArrayList<>();

    @OneToMany(mappedBy = "daiLy", cascade = CascadeType.ALL)
    private List<DonHang> danhSachDonHang = new ArrayList<>();

    @OneToMany(mappedBy = "daiLy", cascade = CascadeType.ALL)
    private List<LichBaoDuong> danhSachLichBaoDuong = new ArrayList<>();
}
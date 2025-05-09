package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khuyen_mai")
    private Long id;

    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam_gia", nullable = false)
    private LoaiGiamGia loaiGiamGia;

    @Column(name = "gia_tri_giam", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "ma_khuyen_mai", unique = true, length = 20)
    private String maKhuyenMai;

    @Enumerated(EnumType.STRING)
    @Column(name = "ap_dung_cho", nullable = false)
    private LoaiApDung apDungCho;

    @Column(name = "gia_tri_toi_thieu", precision = 12, scale = 2)
    private BigDecimal giaTriToiThieu;

    @Column(name = "gioi_han_su_dung")
    private Integer gioiHanSuDung;

    @Column(name = "so_lan_da_dung")
    private Integer soLanDaDung = 0;

    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DieuKienKhuyenMai> danhSachDieuKien = new ArrayList<>();

    public enum LoaiGiamGia {
        phan_tram, so_tien_co_dinh, tuy_chon_mien_phi
    }

    public enum LoaiApDung {
        tat_ca_mau, mau_cu_the, dong_cu_the
    }

    // Kiểm tra khuyến mãi có còn hiệu lực không
    public boolean conHieuLuc() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc) && 
               (gioiHanSuDung == null || soLanDaDung < gioiHanSuDung);
    }

    // Kiểm tra có thể áp dụng với giá trị đơn hàng không
    public boolean hợpLeChoGiaTriDonHang(BigDecimal giaTriDonHang) {
        return giaTriToiThieu == null || giaTriDonHang.compareTo(giaTriToiThieu) >= 0;
    }
}
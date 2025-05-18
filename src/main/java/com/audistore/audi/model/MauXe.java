package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mau_xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_dong", nullable = false)
    private DongXe dongXe;

    @Column(name = "ten_mau", nullable = false)
    private String tenMau;

    @Column(name = "nam_san_xuat", nullable = false)
    private Integer namSanXuat;

    @Column(name = "gia_co_ban", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaCoban;

    @Column(name = "mo_ta")
    private String moTa;

    // Sửa chú thích @Type thành định nghĩa JSON thuần MySQL
    @Column(name = "thong_so_ky_thuat", columnDefinition = "json")
    private String thongSoKyThuat;

    @Column(name = "con_hang")
    private Boolean conHang = true;

    @Column(name = "ngay_ra_mat")
    private LocalDate ngayRaMat;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "mauXe", cascade = CascadeType.ALL)
    private List<HinhAnhXe> danhSachHinhAnh = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "mau_xe_tuy_chon",
            joinColumns = @JoinColumn(name = "id_mau"),
            inverseJoinColumns = @JoinColumn(name = "id_tuy_chon")
    )
    private List<TuyChon> danhSachTuyChon = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "mau_xe_mau_sac",
            joinColumns = @JoinColumn(name = "id_mau"),
            inverseJoinColumns = @JoinColumn(name = "id_mau_sac")
    )
    private List<MauSac> danhSachMauSac = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "mau_xe_noi_that",
            joinColumns = @JoinColumn(name = "id_mau"),
            inverseJoinColumns = @JoinColumn(name = "id_noi_that")
    )
    private List<NoiThat> danhSachNoiThat = new ArrayList<>();

    @OneToMany(mappedBy = "mauXe", cascade = CascadeType.ALL)
    private List<TonKho> danhSachTonKho = new ArrayList<>();

    @OneToMany(mappedBy = "mauXe", cascade = CascadeType.ALL)
    private List<LaiThu> danhSachLaiThu = new ArrayList<>();

    @OneToMany(mappedBy = "mauXe", cascade = CascadeType.ALL)
    private List<CauHinhTuyChiNh> danhSachCauHinh = new ArrayList<>();

    @OneToMany(mappedBy = "mauXe", cascade = CascadeType.ALL)
    private List<MauXeNoiThat> danhSachMauXeNoiThat = new ArrayList<>();
}
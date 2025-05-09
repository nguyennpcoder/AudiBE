package com.audistore.audi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cau_hinh_tuy_chinh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CauHinhTuyChiNh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cau_hinh")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac", nullable = false)
    private MauSac mauSac;

    @Column(name = "ten")
    private String ten;

    @Column(name = "tong_gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongGia;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToMany
    @JoinTable(
            name = "cau_hinh_tuy_chon",
            joinColumns = @JoinColumn(name = "id_cau_hinh"),
            inverseJoinColumns = @JoinColumn(name = "id_tuy_chon")
    )
    private List<TuyChon> danhSachTuyChon = new ArrayList<>();

    @OneToMany(mappedBy = "cauHinh", cascade = CascadeType.ALL)
    private List<DonHang> danhSachDonHang = new ArrayList<>();
}
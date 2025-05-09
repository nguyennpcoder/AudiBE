package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nhat_ky_hoat_dong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhatKyHoatDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhat_ky")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @Column(name = "loai_hoat_dong", nullable = false)
    private String loaiHoatDong;

    @Column(name = "chi_tiet_hoat_dong", columnDefinition = "JSON")
    private String chiTietHoatDong;

    @Column(name = "dia_chi_ip")
    private String diaChiIp;

    @Column(name = "thiet_bi")
    private String thietBi;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}
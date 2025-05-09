package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dang_ky_nhan_tin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangKyNhanTin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dang_ky")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "quan_tam", columnDefinition = "JSON")
    private String quanTam;

    @Column(name = "dang_hoat_dong")
    private Boolean dangHoatDong = true;

    @Column(name = "ngay_dang_ky")
    private LocalDateTime ngayDangKy = LocalDateTime.now();
}
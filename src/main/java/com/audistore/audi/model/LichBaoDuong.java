package com.audistore.audi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_bao_duong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichBaoDuong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich_hen")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_dai_ly", nullable = false)
    private DaiLy daiLy;

    @Column(name = "so_khung", nullable = false)
    private String soKhung;

    @Column(name = "ngay_hen", nullable = false)
    private LocalDateTime ngayHen;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_dich_vu", nullable = false)
    private LoaiDichVu loaiDichVu;

    @Column(name = "mo_ta")
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.da_dat_lich;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    public enum LoaiDichVu {
        bao_duong_dinh_ky, sua_chua, bao_hanh, trieu_hoi
    }

    public enum TrangThai {
        da_dat_lich, dang_thuc_hien, hoan_thanh, da_huy
    }
}
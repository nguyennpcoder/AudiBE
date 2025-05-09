package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lai_thu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaiThu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lai_thu")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_mau", nullable = false)
    private MauXe mauXe;

    @ManyToOne
    @JoinColumn(name = "id_dai_ly", nullable = false)
    private DaiLy daiLy;

    @Column(name = "thoi_gian_hen", nullable = false)
    private LocalDateTime thoiGianHen;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.cho_duyet;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    public enum TrangThai {
        cho_duyet, da_xac_nhan, hoan_thanh, da_huy
    }
}

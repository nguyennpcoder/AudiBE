package com.audistore.audi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thanh_toan")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_don_hang", nullable = false)
    private DonHang donHang;

    @Column(name = "so_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_thanh_toan", nullable = false)
    private LoaiThanhToan loaiThanhToan;

    @CreationTimestamp
    @Column(name = "ngay_thanh_toan", updatable = false)
    private LocalDateTime ngayThanhToan;

    @Column(name = "phuong_thuc", nullable = false)
    private String phuongThuc;

    @Column(name = "ma_giao_dich")
    private String maGiaoDich;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.dang_xu_ly;

    public enum LoaiThanhToan {
        dat_coc, tra_gop, toan_bo, hoan_tien
    }

    public enum TrangThai {
        dang_xu_ly, hoan_thanh, that_bai, da_hoan_tien
    }
}
package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

@Entity
@Table(name = "binh_luan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinhLuan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_binh_luan")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bai_viet", nullable = false)
    private BaiViet baiViet;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @CreationTimestamp
    @Column(name = "ngay_binh_luan", updatable = false)
    private LocalDateTime ngayBinhLuan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.hien_thi;

    @ManyToOne
    @JoinColumn(name = "id_binh_luan_cha")
    private BinhLuan binhLuanCha;

    @OneToMany(mappedBy = "binhLuanCha", cascade = CascadeType.ALL)
    private List<BinhLuan> binhLuanCon;

    @Column(name = "so_luot_thich")
    private Integer soLuotThich = 0;

    // Sử dụng bảng binh_luan_likes để lưu trạng thái like
    @ElementCollection
    @CollectionTable(name = "binh_luan_likes", joinColumns = @JoinColumn(name = "id_binh_luan"))
    @Column(name = "id_nguoi_dung")
    private List<Long> nguoiDungThich = new ArrayList<>();

    public enum TrangThai {
        hien_thi, an, xoa
    }
} 
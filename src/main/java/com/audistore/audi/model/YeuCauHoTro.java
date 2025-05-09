package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "yeu_cau_ho_tro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauHoTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_yeu_cau")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.moi;

    @Enumerated(EnumType.STRING)
    @Column(name = "muc_do_uu_tien")
    private MucDoUuTien mucDoUuTien = MucDoUuTien.trung_binh;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne
    @JoinColumn(name = "nguoi_phu_trach")
    private NguoiDung nguoiPhuTrach;

    @OneToMany(mappedBy = "yeuCauHoTro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhanHoiYeuCau> danhSachPhanHoi = new ArrayList<>();

    public enum TrangThai {
        moi, dang_xu_ly, da_giai_quyet, dong
    }

    public enum MucDoUuTien {
        thap, trung_binh, cao, khan_cap
    }
}
package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ho_so_tra_gop")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoSoTraGop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ho_so")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_don_hang", nullable = false)
    private DonHang donHang;

    @Column(name = "ngan_hang_doi_tac")
    private String nganHangDoiTac;

    @Column(name = "so_tien_vay", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTienVay;

    @Column(name = "lai_suat", nullable = false, precision = 5, scale = 2)
    private BigDecimal laiSuat;

    @Column(name = "ky_han_thang", nullable = false)
    private Integer kyHanThang;

    @Column(name = "tra_hang_thang", nullable = false, precision = 10, scale = 2)
    private BigDecimal traHangThang;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.dang_xu_ly;

    @CreationTimestamp
    @Column(name = "ngay_nop_ho_so", updatable = false)
    private LocalDateTime ngayNopHoSo;

    @Column(name = "ngay_quyet_dinh")
    private LocalDateTime ngayQuyetDinh;
    
    // Thêm trường ghi chú
    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
    
    // Thêm trường lý do từ chối
    @Column(name = "ly_do_tu_choi", columnDefinition = "TEXT")
    private String lyDoTuChoi;
    
    // Thông tin người mua hộ
    @Column(name = "nguoi_mua_ho")
    private String nguoiMuaHo;
    
    @Column(name = "so_dien_thoai_nguoi_mua_ho")
    private String soDienThoaiNguoiMuaHo;
    
    @Column(name = "email_nguoi_mua_ho")
    private String emailNguoiMuaHo;
    
    @Column(name = "dia_chi_nguoi_mua_ho", columnDefinition = "TEXT")
    private String diaChiNguoiMuaHo;

    public enum TrangThai {
        dang_xu_ly, da_phe_duyet, bi_tu_choi, hoan_thanh
    }
}
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
@Table(name = "don_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_don_hang")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "id_ton_kho")
    private TonKho tonKho;

    @ManyToOne
    @JoinColumn(name = "id_cau_hinh")
    private CauHinhTuyChiNh cauHinh;

    @ManyToOne
    @JoinColumn(name = "id_dai_ly", nullable = false)
    private DaiLy daiLy;

    @CreationTimestamp
    @Column(name = "ngay_dat", updatable = false)
    private LocalDateTime ngayDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.cho_xu_ly;

    @Column(name = "tien_dat_coc", precision = 12, scale = 2)
    private BigDecimal tienDatCoc;

    @Column(name = "tong_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "ngay_giao_du_kien")
    private LocalDate ngayGiaoDuKien;

    @Column(name = "ngay_giao_thuc_te")
    private LocalDate ngayGiaoThucTe;

    @Column(name = "phuong_thuc_thanh_toan")
    private String phuongThucThanhToan;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ThanhToan> danhSachThanhToan = new ArrayList<>();

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<HoSoTraGop> danhSachHoSoTraGop = new ArrayList<>();
    
    // Thêm khuyến mãi
    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai")
    private KhuyenMai khuyenMai;

    @Column(name = "tien_giam", precision = 12, scale = 2)
    private BigDecimal tienGiam;

    public enum TrangThai {
        cho_xu_ly, da_dat_coc, dang_xu_ly, san_sang_giao, da_giao, da_huy
    }
}
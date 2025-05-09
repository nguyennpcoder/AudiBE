package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "nguoi_dung")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguoi_dung")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "mat_khau_ma_hoa", nullable = false)
    private String matKhauMaHoa;

    @Column(name = "ho", nullable = false)
    private String ho;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "thanh_pho")
    private String thanhPho;

    @Column(name = "tinh")
    private String tinh;

    @Column(name = "ma_buu_dien")
    private String maBuuDien;

    @Column(name = "quoc_gia")
    private String quocGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro")
    private VaiTro vaiTro = VaiTro.khach_hang;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @ManyToMany
    @JoinTable(
        name = "nguoi_dung_nhom_quyen",
        joinColumns = @JoinColumn(name = "id_nguoi_dung"),
        inverseJoinColumns = @JoinColumn(name = "id_nhom_quyen")
    )
    private Set<NhomQuyen> nhomQuyen = new HashSet<>();

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL)
    private List<LaiThu> danhSachLaiThu = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL)
    private List<CauHinhTuyChiNh> danhSachCauHinh = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL)
    private List<DonHang> danhSachDonHang = new ArrayList<>();

    public enum VaiTro {
        khach_hang, quan_tri, ban_hang, ho_tro
    }
}
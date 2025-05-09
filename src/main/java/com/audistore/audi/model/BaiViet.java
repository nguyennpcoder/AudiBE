package com.audistore.audi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bai_viet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaiViet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bai_viet")
    private Long id;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @ManyToOne
    @JoinColumn(name = "id_tac_gia", nullable = false)
    private NguoiDung tacGia;

    @CreationTimestamp
    @Column(name = "ngay_dang", updatable = false)
    private LocalDateTime ngayDang;

    @Column(name = "da_xuat_ban")
    private Boolean daXuatBan = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "danh_muc", nullable = false)
    private DanhMuc danhMuc;

    @Column(name = "the_gan", columnDefinition = "json")
    private String theGan;

    public enum DanhMuc {
        tin_tuc, danh_gia, cong_nghe, su_kien, meo, phong_cach_song
    }
}
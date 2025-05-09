package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dieu_kien_khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DieuKienKhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai", nullable = false)
    private KhuyenMai khuyenMai;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_doi_tuong", nullable = false)
    private LoaiDoiTuong loaiDoiTuong;

    @Column(name = "id_doi_tuong", nullable = false)
    private Long idDoiTuong;

    public enum LoaiDoiTuong {
        mau_xe, dong_xe, tuy_chon
    }
}
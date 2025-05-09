package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mat_khau_nguoi_dung")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatKhauNguoiDung {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_nguoi_dung", nullable = false)
    private Long idNguoiDung;
    
    @Column(name = "mat_khau_goc", nullable = false)
    private String matKhauGoc;
    
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}
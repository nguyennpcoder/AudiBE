package com.audistore.audi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "doanh_thu_khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThuKhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idKhuyenMai;
    private Integer soLanSuDung;
    private BigDecimal doanhThuTaoRa;
    private Timestamp lastUpdate;

}
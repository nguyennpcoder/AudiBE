package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quyen")
    private Long id;

    @Column(name = "ten", nullable = false, unique = true)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ma_quyen", nullable = false, unique = true)
    private String maQuyen;

    @ManyToMany(mappedBy = "danhSachQuyen")
    private Set<NhomQuyen> nhomQuyen = new HashSet<>();
}
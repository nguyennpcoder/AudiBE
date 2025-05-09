package com.audistore.audi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nhom_quyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhomQuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhom_quyen")
    private Long id;

    @Column(name = "ten", nullable = false, unique = true)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @ManyToMany
    @JoinTable(
        name = "nhom_quyen_quyen",
        joinColumns = @JoinColumn(name = "id_nhom_quyen"),
        inverseJoinColumns = @JoinColumn(name = "id_quyen")
    )
    private Set<Quyen> danhSachQuyen = new HashSet<>();

    @ManyToMany(mappedBy = "nhomQuyen")
    private Set<NguoiDung> nguoiDung = new HashSet<>();
}
package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhomQuyenDTO {
    private Long id;
    private String ten;
    private String moTa;
    private Set<QuyenDTO> danhSachQuyen = new HashSet<>();
}
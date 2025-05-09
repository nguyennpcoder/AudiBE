package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungQuyenDTO {
    private Long idNguoiDung;
    private Set<Long> idNhomQuyen = new HashSet<>();
}
package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuyenDTO {
    private Long id;
    private String ten;
    private String moTa;
    private String maQuyen;
}
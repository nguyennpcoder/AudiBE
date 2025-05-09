package com.audistore.audi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DieuKienKhuyenMaiDTO {
    private Long id;
    private Long idKhuyenMai;
    
    @NotNull(message = "Loại đối tượng không được để trống")
    private String loaiDoiTuong;
    
    @NotNull(message = "ID đối tượng không được để trống")
    private Long idDoiTuong;
    
    private String tenDoiTuong;
}
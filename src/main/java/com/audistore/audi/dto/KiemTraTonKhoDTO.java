package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KiemTraTonKhoDTO {
    private Long idMau;
    private String tenMau;
    private Integer tongSoLuong;
    private Integer soLuongCoSan;
    private List<TonKhoTheoDaiLyDTO> chiTietTheoDaiLy;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TonKhoTheoDaiLyDTO {
        private Long idDaiLy;
        private String tenDaiLy;
        private String thanhPho;
        private Integer soLuongCoSan;
        private String soDienThoai;
    }
}
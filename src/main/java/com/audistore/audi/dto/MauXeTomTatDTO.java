package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Lightweight car model row used by the /models overview page.
 *
 * It intentionally omits the colour/option ID lists, which forced the
 * full entity graph to be loaded for every model (N+1 queries) and made
 * GET /api/v1/mau-xe take several seconds.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXeTomTatDTO {
    private Long id;
    private Long idDong;
    private String tenDong;
    private String tenMau;
    private Integer namSanXuat;
    private BigDecimal giaCoban;
    private Boolean conHang;
}

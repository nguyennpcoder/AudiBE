package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cover image for one car model. Used by the /models page to avoid one
 * image request per model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXeAnhDaiDienDTO {
    private Long idMauXe;
    private String duongDanAnh;
}

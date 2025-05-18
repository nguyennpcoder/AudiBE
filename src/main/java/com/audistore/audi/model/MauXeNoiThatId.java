package com.audistore.audi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauXeNoiThatId implements Serializable {

    @Column(name = "id_mau")
    private Long idMau;

    @Column(name = "id_noi_that")
    private Long idNoiThat;
}
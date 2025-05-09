package com.audistore.audi.service.specification;

import com.audistore.audi.model.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class SearchSpecifications {

    // Specifications cho MauXe
    public static Specification<MauXe> withKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("tenMau")), likePattern),
                    cb.like(cb.lower(root.get("moTa")), likePattern)
            );
        };
    }
    
    public static Specification<MauXe> inDongXeList(List<Long> dongXeIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(dongXeIds)) {
                return cb.conjunction();
            }
            return root.get("dongXe").get("id").in(dongXeIds);
        };
    }
    
    public static Specification<MauXe> inPhanLoaiList(List<String> phanLoaiList) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(phanLoaiList)) {
                return cb.conjunction();
            }
            return root.get("dongXe").get("phanLoai").in(
                    phanLoaiList.stream()
                            .map(DongXe.PhanLoai::valueOf)
                            .toList()
            );
        };
    }
    
    public static Specification<MauXe> betweenNamSanXuat(Integer namTu, Integer namDen) {
        return (root, query, cb) -> {
            if (namTu == null && namDen == null) {
                return cb.conjunction();
            }
            if (namTu != null && namDen != null) {
                return cb.between(root.get("namSanXuat"), namTu, namDen);
            }
            if (namTu != null) {
                return cb.greaterThanOrEqualTo(root.get("namSanXuat"), namTu);
            }
            return cb.lessThanOrEqualTo(root.get("namSanXuat"), namDen);
        };
    }
    
    public static Specification<MauXe> betweenGia(BigDecimal giaTu, BigDecimal giaDen) {
        return (root, query, cb) -> {
            if (giaTu == null && giaDen == null) {
                return cb.conjunction();
            }
            if (giaTu != null && giaDen != null) {
                return cb.between(root.get("giaCoban"), giaTu, giaDen);
            }
            if (giaTu != null) {
                return cb.greaterThanOrEqualTo(root.get("giaCoban"), giaTu);
            }
            return cb.lessThanOrEqualTo(root.get("giaCoban"), giaDen);
        };
    }
    
    public static Specification<MauXe> withConHang(Boolean conHang) {
        return (root, query, cb) -> {
            if (conHang == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("conHang"), conHang);
        };
    }
    
    // Specifications cho DaiLy
    public static Specification<DaiLy> withDaiLyKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("ten")), likePattern),
                    cb.like(cb.lower(root.get("diaChi")), likePattern),
                    cb.like(cb.lower(root.get("thanhPho")), likePattern),
                    cb.like(cb.lower(root.get("tinh")), likePattern)
            );
        };
    }
    
    public static Specification<DaiLy> inThanhPhoList(List<String> thanhPhoList) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(thanhPhoList)) {
                return cb.conjunction();
            }
            return root.get("thanhPho").in(thanhPhoList);
        };
    }
    
    public static Specification<DaiLy> inTinhList(List<String> tinhList) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(tinhList)) {
                return cb.conjunction();
            }
            return root.get("tinh").in(tinhList);
        };
    }
    
    public static Specification<DaiLy> withLaTrungTamDichVu(Boolean laTrungTamDichVu) {
        return (root, query, cb) -> {
            if (laTrungTamDichVu == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("laTrungTamDichVu"), laTrungTamDichVu);
        };
    }
    
    // Specifications cho TonKho
    public static Specification<TonKho> withTonKhoKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("soKhung")), likePattern),
                    cb.like(cb.lower(root.get("tiNhNangThem")), likePattern),
                    cb.like(cb.lower(root.get("mauXe").get("tenMau")), likePattern),
                    cb.like(cb.lower(root.get("daiLy").get("ten")), likePattern)
            );
        };
    }
    
    public static Specification<TonKho> inTrangThaiList(List<String> trangThaiList) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(trangThaiList)) {
                return cb.conjunction();
            }
            return root.get("trangThai").in(
                    trangThaiList.stream()
                            .map(TonKho.TrangThai::valueOf)
                            .toList()
            );
        };
    }
    
    public static Specification<TonKho> inMauSacList(List<Long> mauSacIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(mauSacIds)) {
                return cb.conjunction();
            }
            return root.get("mauSac").get("id").in(mauSacIds);
        };
    }
    
    public static Specification<TonKho> inDaiLyList(List<Long> daiLyIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(daiLyIds)) {
                return cb.conjunction();
            }
            return root.get("daiLy").get("id").in(daiLyIds);
        };
    }
    
    public static Specification<TonKho> inMauXeList(List<Long> mauXeIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(mauXeIds)) {
                return cb.conjunction();
            }
            return root.get("mauXe").get("id").in(mauXeIds);
        };
    }
}
package com.audistore.audi.service;

import com.audistore.audi.dto.ThongKeDashboardDTO;
import com.audistore.audi.dto.ThongKeDashboardDTO.SanPhamThongKeDTO;
import com.audistore.audi.model.HinhAnhXe;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NhatKyXemSanPhamRepository;
import com.audistore.audi.repository.ThongKeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThongKeDashboardService {
    
    @Autowired
    private ThongKeRepository thongKeRepository;
    
    @Autowired
    private NhatKyXemSanPhamRepository nhatKyXemSanPhamRepository;
    
    @Autowired
    private MauXeRepository mauXeRepository;
    
    private static final int TOP_PRODUCTS_LIMIT = 10;
    
    public ThongKeDashboardDTO getThongKeDashboard() {
        ThongKeDashboardDTO dashboardDTO = new ThongKeDashboardDTO();
        
        // Thống kê tổng quan
        dashboardDTO.setTongDoanhThu(thongKeRepository.sumTongDoanhThu() != null ? 
                thongKeRepository.sumTongDoanhThu() : BigDecimal.ZERO);
        dashboardDTO.setTongSoDonHang(thongKeRepository.countDonHangThanhCong());
        dashboardDTO.setTongSoSanPham(thongKeRepository.countTongSoSanPham());
        dashboardDTO.setTongSoNguoiDung(thongKeRepository.countTongSoNguoiDung());
        
        // Thống kê sản phẩm bán chạy
        List<Map<String, Object>> sanPhamBanChayData = thongKeRepository.thongKeSanPhamBanChay(TOP_PRODUCTS_LIMIT);
        List<SanPhamThongKeDTO> sanPhamBanChay = mapToSanPhamThongKeList(sanPhamBanChayData);
        dashboardDTO.setSanPhamBanChay(sanPhamBanChay);
        
        // Thống kê sản phẩm xem nhiều
        List<Map<String, Object>> sanPhamXemNhieuData = nhatKyXemSanPhamRepository.thongKeSanPhamXemNhieu(TOP_PRODUCTS_LIMIT);
        List<SanPhamThongKeDTO> sanPhamXemNhieu = mapToSanPhamXemNhieuList(sanPhamXemNhieuData);
        dashboardDTO.setSanPhamXemNhieu(sanPhamXemNhieu);
        
        // Thống kê theo thời gian
        dashboardDTO.setDoanhThuTheoThang(thongKeRepository.thongKeDoanhThuTheoThang());
        dashboardDTO.setDonHangTheoThang(thongKeRepository.thongKeDonHangTheoThang());
        dashboardDTO.setLuotXemTheoThang(nhatKyXemSanPhamRepository.thongKeLuotXemTheoThang());
        
        // Tính tỷ lệ chuyển đổi
        Long totalViews = nhatKyXemSanPhamRepository.countNguoiDungXemSanPham();
        Long totalOrders = thongKeRepository.countDonHangThanhCong();
        
        double conversionRate = 0.0;
        if (totalViews != null && totalViews > 0 && totalOrders != null) {
            conversionRate = (double) totalOrders / totalViews * 100;
        }
        dashboardDTO.setTyLeChuyenDoi(conversionRate);
        
        return dashboardDTO;
    }
    
    private List<SanPhamThongKeDTO> mapToSanPhamThongKeList(List<Map<String, Object>> data) {
        List<SanPhamThongKeDTO> result = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            SanPhamThongKeDTO dto = new SanPhamThongKeDTO();
            
            // Xử lý trường hợp null hoặc đổi kiểu dữ liệu
            if (item.get("idMauXe") != null) {
                if (item.get("idMauXe") instanceof Number) {
                    dto.setIdMauXe(((Number) item.get("idMauXe")).longValue());
                } else {
                    dto.setIdMauXe(Long.valueOf(item.get("idMauXe").toString()));
                }
            }
            
            if (item.get("tenMauXe") != null) {
                dto.setTenMauXe(item.get("tenMauXe").toString());
            }
            
            if (item.get("soLuong") != null) {
                if (item.get("soLuong") instanceof Number) {
                    dto.setSoLuong(((Number) item.get("soLuong")).longValue());
                } else {
                    dto.setSoLuong(Long.valueOf(item.get("soLuong").toString()));
                }
            }
            
            if (item.get("doanhThu") != null) {
                if (item.get("doanhThu") instanceof BigDecimal) {
                    dto.setDoanhThu((BigDecimal) item.get("doanhThu"));
                } else if (item.get("doanhThu") instanceof Number) {
                    dto.setDoanhThu(BigDecimal.valueOf(((Number) item.get("doanhThu")).doubleValue()));
                } else {
                    dto.setDoanhThu(new BigDecimal(item.get("doanhThu").toString()));
                }
            }
            
            // Lấy ảnh đại diện cho mẫu xe
            if (dto.getIdMauXe() != null) {
                MauXe mauXe = mauXeRepository.findById(dto.getIdMauXe()).orElse(null);
                if (mauXe != null && mauXe.getDanhSachHinhAnh() != null && !mauXe.getDanhSachHinhAnh().isEmpty()) {
                    List<HinhAnhXe> anhThuNho = mauXe.getDanhSachHinhAnh().stream()
                            .filter(a -> a.getLoaiHinh() != null && a.getLoaiHinh().equals(HinhAnhXe.LoaiHinh.thu_nho))
                            .collect(Collectors.toList());
                    
                    if (!anhThuNho.isEmpty()) {
                        dto.setAnhDaiDien(anhThuNho.get(0).getDuongDanAnh());
                    } else if (!mauXe.getDanhSachHinhAnh().isEmpty()) {
                        // Nếu không có ảnh thu nhỏ, lấy ảnh đầu tiên
                        dto.setAnhDaiDien(mauXe.getDanhSachHinhAnh().get(0).getDuongDanAnh());
                    }
                }
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    private List<SanPhamThongKeDTO> mapToSanPhamXemNhieuList(List<Map<String, Object>> data) {
        List<SanPhamThongKeDTO> result = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            SanPhamThongKeDTO dto = new SanPhamThongKeDTO();
            
            Object idMauXeObj = item.get("idMauXe");
            if (idMauXeObj != null) {
                // Xử lý chuỗi JSON được trích xuất từ trường JSON
                String idMauXeStr = idMauXeObj.toString().replace("\"", "");
                try {
                    dto.setIdMauXe(Long.valueOf(idMauXeStr));
                    
                    // Lấy thông tin mẫu xe
                    MauXe mauXe = mauXeRepository.findById(dto.getIdMauXe()).orElse(null);
                    if (mauXe != null) {
                        dto.setTenMauXe(mauXe.getTenMau());
                        
                        // Lấy ảnh đại diện
                        if (mauXe.getDanhSachHinhAnh() != null && !mauXe.getDanhSachHinhAnh().isEmpty()) {
                            List<HinhAnhXe> anhThuNho = mauXe.getDanhSachHinhAnh().stream()
                                    .filter(a -> a.getLoaiHinh() != null && a.getLoaiHinh().equals(HinhAnhXe.LoaiHinh.thu_nho))
                                    .collect(Collectors.toList());
                            
                            if (!anhThuNho.isEmpty()) {
                                dto.setAnhDaiDien(anhThuNho.get(0).getDuongDanAnh());
                            } else if (!mauXe.getDanhSachHinhAnh().isEmpty()) {
                                // Nếu không có ảnh thu nhỏ, lấy ảnh đầu tiên
                                dto.setAnhDaiDien(mauXe.getDanhSachHinhAnh().get(0).getDuongDanAnh());
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Không thể chuyển đổi giá trị idMauXe: " + idMauXeStr);
                    continue;
                }
            }
            
            if (item.get("luotXem") != null) {
                if (item.get("luotXem") instanceof Number) {
                    dto.setLuotXem(((Number) item.get("luotXem")).longValue());
                } else {
                    dto.setLuotXem(Long.valueOf(item.get("luotXem").toString()));
                }
            }
            
            result.add(dto);
        }
        
        return result;
    }
}
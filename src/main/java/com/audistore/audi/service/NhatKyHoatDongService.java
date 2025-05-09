package com.audistore.audi.service;

import com.audistore.audi.dto.NhatKyHoatDongDTO;
import com.audistore.audi.dto.ThongKeHoatDongDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.model.NhatKyHoatDong;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.repository.NhatKyHoatDongRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class NhatKyHoatDongService {
    
    private final NhatKyHoatDongRepository nhatKyHoatDongRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    public NhatKyHoatDongService(
            NhatKyHoatDongRepository nhatKyHoatDongRepository,
            NguoiDungRepository nguoiDungRepository,
            ObjectMapper objectMapper) {
        this.nhatKyHoatDongRepository = nhatKyHoatDongRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.objectMapper = objectMapper;
    }
    
    public void logHoatDong(String loaiHoatDong, Map<String, Object> chiTietHoatDong, 
                            HttpServletRequest request, Long idNguoiDung) {
        NhatKyHoatDong nhatKy = new NhatKyHoatDong();
        
        // Set người dùng nếu có
        if (idNguoiDung != null) {
            NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                    .orElse(null);
            nhatKy.setNguoiDung(nguoiDung);
        }
        
        nhatKy.setLoaiHoatDong(loaiHoatDong);
        
        // Chuyển đổi chi tiết hoạt động sang JSON
        try {
            nhatKy.setChiTietHoatDong(objectMapper.writeValueAsString(chiTietHoatDong));
        } catch (JsonProcessingException e) {
            nhatKy.setChiTietHoatDong("{}");
        }
        
        // Lấy thông tin IP và thiết bị
        nhatKy.setDiaChiIp(getClientIp(request));
        nhatKy.setThietBi(request.getHeader("User-Agent"));
        
        nhatKyHoatDongRepository.save(nhatKy);
    }
    
    // Overload method để lấy current user từ Security Context
    public void logHoatDong(String loaiHoatDong, Map<String, Object> chiTietHoatDong, 
                           HttpServletRequest request) {
        Long idNguoiDung = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
            // Lấy thông tin người dùng từ UserDetailsImpl
            try {
                com.audistore.audi.security.service.UserDetailsImpl userDetails = 
                        (com.audistore.audi.security.service.UserDetailsImpl) authentication.getPrincipal();
                idNguoiDung = userDetails.getId();
            } catch (Exception e) {
                // Xử lý nếu có lỗi khi lấy thông tin người dùng
            }
        }
        
        logHoatDong(loaiHoatDong, chiTietHoatDong, request, idNguoiDung);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !xForwardedFor.equalsIgnoreCase("unknown")) {
            int index = xForwardedFor.indexOf(',');
            if (index > 0) {
                return xForwardedFor.substring(0, index);
            } else {
                return xForwardedFor;
            }
        }
        
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !ip.equalsIgnoreCase("unknown")) {
            return ip;
        }
        
        return request.getRemoteAddr();
    }
    
    public Page<NhatKyHoatDongDTO> getAllNhatKyHoatDong(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<NhatKyHoatDong> nhatKyPage = nhatKyHoatDongRepository.findAll(pageable);
        return nhatKyPage.map(this::mapToDTO);
    }
    
    public NhatKyHoatDongDTO getNhatKyHoatDongById(Long id) {
        return nhatKyHoatDongRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhật ký hoạt động với ID: " + id));
    }
    
    public Page<NhatKyHoatDongDTO> getNhatKyByNguoiDung(Long idNguoiDung, int page, int size) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiDung));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<NhatKyHoatDong> nhatKyPage = nhatKyHoatDongRepository.findByNguoiDung(nguoiDung, pageable);
        
        return nhatKyPage.map(this::mapToDTO);
    }
    
    public Page<NhatKyHoatDongDTO> getNhatKyByLoaiHoatDong(String loaiHoatDong, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<NhatKyHoatDong> nhatKyPage = nhatKyHoatDongRepository.findByLoaiHoatDong(loaiHoatDong, pageable);
        
        return nhatKyPage.map(this::mapToDTO);
    }
    
    public Page<NhatKyHoatDongDTO> getNhatKyByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<NhatKyHoatDong> nhatKyPage = nhatKyHoatDongRepository.findByNgayTaoBetween(startDateTime, endDateTime, pageable);
        
        return nhatKyPage.map(this::mapToDTO);
    }
    
    public List<String> getLoaiHoatDongList() {
        return nhatKyHoatDongRepository.findDistinctLoaiHoatDong();
    }
    
    public ThongKeHoatDongDTO getThongKeHoatDong() {
        ThongKeHoatDongDTO thongKe = new ThongKeHoatDongDTO();
        
        // Tổng số hoạt động
        thongKe.setTongSoHoatDong(nhatKyHoatDongRepository.count());
        
        // Thống kê theo loại hoạt động
        List<String> loaiHoatDongs = nhatKyHoatDongRepository.findDistinctLoaiHoatDong();
        List<Map<String, Object>> thongKeTheoLoai = new ArrayList<>();
        
        for (String loai : loaiHoatDongs) {
            Map<String, Object> item = new HashMap<>();
            item.put("loaiHoatDong", loai);
            item.put("soLuong", nhatKyHoatDongRepository.countByLoaiHoatDong(loai));
            thongKeTheoLoai.add(item);
        }
        thongKe.setThongKeTheoLoai(thongKeTheoLoai);
        
        // Thống kê theo người dùng (top 10 người dùng hoạt động nhiều nhất)
        List<NguoiDung> nguoiDungs = nguoiDungRepository.findAll();
        List<Map<String, Object>> thongKeTheoNguoiDung = new ArrayList<>();
        
        for (NguoiDung nguoiDung : nguoiDungs) {
            Long soHoatDong = nhatKyHoatDongRepository.countByNguoiDungId(nguoiDung.getId());
            if (soHoatDong > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("idNguoiDung", nguoiDung.getId());
                item.put("tenNguoiDung", nguoiDung.getHo() + " " + nguoiDung.getTen());
                item.put("email", nguoiDung.getEmail());
                item.put("soHoatDong", soHoatDong);
                thongKeTheoNguoiDung.add(item);
            }
        }
        
        // Sắp xếp theo số hoạt động giảm dần và lấy top 10
        thongKeTheoNguoiDung.sort((a, b) -> 
            ((Long)b.get("soHoatDong")).compareTo((Long)a.get("soHoatDong")));
        
        if (thongKeTheoNguoiDung.size() > 10) {
            thongKeTheoNguoiDung = thongKeTheoNguoiDung.subList(0, 10);
        }
        
        thongKe.setThongKeTheoNguoiDung(thongKeTheoNguoiDung);
        
        // Thống kê theo ngày (7 ngày gần nhất)
        List<Map<String, Object>> thongKeTheoNgay = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            long count = nhatKyHoatDongRepository.findByNgayTaoBetween(startOfDay, endOfDay).size();
            
            Map<String, Object> item = new HashMap<>();
            item.put("ngay", date.toString());
            item.put("soHoatDong", count);
            thongKeTheoNgay.add(item);
        }
        
        thongKe.setThongKeTheoNgay(thongKeTheoNgay);
        
        return thongKe;
    }
    
    private NhatKyHoatDongDTO mapToDTO(NhatKyHoatDong nhatKy) {
        NhatKyHoatDongDTO dto = new NhatKyHoatDongDTO();
        dto.setId(nhatKy.getId());
        
        // Thông tin người dùng
        if (nhatKy.getNguoiDung() != null) {
            dto.setIdNguoiDung(nhatKy.getNguoiDung().getId());
            dto.setTenNguoiDung(nhatKy.getNguoiDung().getHo() + " " + nhatKy.getNguoiDung().getTen());
            dto.setEmailNguoiDung(nhatKy.getNguoiDung().getEmail());
        }
        
        dto.setLoaiHoatDong(nhatKy.getLoaiHoatDong());
        
        // Chuyển JSON thành Map
        try {
            if (nhatKy.getChiTietHoatDong() != null && !nhatKy.getChiTietHoatDong().isEmpty()) {
                Map<String, Object> chiTiet = objectMapper.readValue(
                        nhatKy.getChiTietHoatDong(), 
                        new TypeReference<Map<String, Object>>() {});
                dto.setChiTietHoatDong(chiTiet);
            } else {
                dto.setChiTietHoatDong(new HashMap<>());
            }
        } catch (JsonProcessingException e) {
            dto.setChiTietHoatDong(new HashMap<>());
        }
        
        dto.setDiaChiIp(nhatKy.getDiaChiIp());
        dto.setThietBi(nhatKy.getThietBi());
        dto.setNgayTao(nhatKy.getNgayTao());
        
        return dto;
    }
}
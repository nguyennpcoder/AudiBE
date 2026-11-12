package com.audistore.audi.service;

import com.audistore.audi.dto.CauHinhTuyChinhDTO;
import com.audistore.audi.dto.TuyChonDTO;
import com.audistore.audi.model.CauHinhTuyChiNh;
import com.audistore.audi.model.MauSac;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.model.TuyChon;
import com.audistore.audi.repository.CauHinhTuyChinhRepository;
import com.audistore.audi.repository.MauSacRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.repository.TuyChonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import com.audistore.audi.model.NoiThat;
import com.audistore.audi.repository.NoiThatRepository;
import com.audistore.audi.model.BanhXe;
import com.audistore.audi.repository.BanhXeRepository;
import com.audistore.audi.dto.KhuyenMaiDTO;
import com.itextpdf.text.pdf.BaseFont;
import java.io.InputStream;
import java.io.File;

@Slf4j
@Service
@Transactional
public class CauHinhTuyChinhService {

    private final CauHinhTuyChinhRepository cauHinhTuyChinhRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final MauXeRepository mauXeRepository;
    private final MauSacRepository mauSacRepository;
    private final TuyChonRepository tuyChonRepository;
    private final NoiThatRepository noiThatRepository;
    private final BanhXeRepository banhXeRepository;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CauHinhTuyChinhService(
            CauHinhTuyChinhRepository cauHinhTuyChinhRepository,
            NguoiDungRepository nguoiDungRepository,
            MauXeRepository mauXeRepository,
            MauSacRepository mauSacRepository,
            TuyChonRepository tuyChonRepository,
            NoiThatRepository noiThatRepository,
            BanhXeRepository banhXeRepository) {
        this.cauHinhTuyChinhRepository = cauHinhTuyChinhRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.mauXeRepository = mauXeRepository;
        this.mauSacRepository = mauSacRepository;
        this.tuyChonRepository = tuyChonRepository;
        this.noiThatRepository = noiThatRepository;
        this.banhXeRepository = banhXeRepository;
    }

    public List<CauHinhTuyChinhDTO> getAllCauHinhTuyChinh() {
        return cauHinhTuyChinhRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CauHinhTuyChinhDTO getCauHinhTuyChinhById(Long id) {
        return cauHinhTuyChinhRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình tùy chỉnh với ID: " + id));
    }
    
    public List<CauHinhTuyChinhDTO> getCauHinhTuyChinhByNguoiDung(Long idNguoiDung) {
        log.info("Bắt đầu tìm cấu hình cho người dùng ID: {}", idNguoiDung);
        
        try {
            List<CauHinhTuyChiNh> cauHinhList = cauHinhTuyChinhRepository.findByNguoiDungId(idNguoiDung);
            log.info("Tìm thấy {} cấu hình cho người dùng ID: {}", cauHinhList.size(), idNguoiDung);
            
            if (cauHinhList.isEmpty()) {
                log.warn("Không tìm thấy cấu hình nào cho người dùng ID: {}", idNguoiDung);
                return new ArrayList<>();
            }
            
            List<CauHinhTuyChinhDTO> result = cauHinhList.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            log.info("Đã chuyển đổi thành công {} cấu hình thành DTO", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("Lỗi khi tìm cấu hình cho người dùng ID {}: {}", idNguoiDung, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm cấu hình: " + e.getMessage(), e);
        }
    }
    
    public List<CauHinhTuyChinhDTO> getCauHinhTuyChinhByMauXe(Long idMauXe) {
        return cauHinhTuyChinhRepository.findByMauXeId(idMauXe).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public CauHinhTuyChinhDTO createCauHinhTuyChinh(CauHinhTuyChinhDTO cauHinhTuyChinhDTO) {
        log.info("Bắt đầu tạo cấu hình tùy chỉnh: {}", cauHinhTuyChinhDTO);
        
        // Kiểm tra dữ liệu đầu vào
        if (cauHinhTuyChinhDTO.getIdMau() == null || cauHinhTuyChinhDTO.getIdMauSac() == null) {
            throw new RuntimeException("Phải chọn mẫu xe và màu sắc");
        }
        
        // Kiểm tra idNguoiDung
        if (cauHinhTuyChinhDTO.getIdNguoiDung() == null) {
            throw new RuntimeException("ID người dùng không được để trống");
        }
        
        // Tính tổng giá nếu không được cung cấp
        if (cauHinhTuyChinhDTO.getTongGia() == null) {
            BigDecimal tongGia = tinhTongGia(
                    cauHinhTuyChinhDTO.getIdMau(), 
                    cauHinhTuyChinhDTO.getIdMauSac(), 
                    cauHinhTuyChinhDTO.getDanhSachIdTuyChon());
            cauHinhTuyChinhDTO.setTongGia(tongGia);
            log.info("Đã tính tổng giá: {}", tongGia);
        }
        
        // Kiểm tra xem người dùng có tồn tại không
        NguoiDung nguoiDung = nguoiDungRepository.findById(cauHinhTuyChinhDTO.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + cauHinhTuyChinhDTO.getIdNguoiDung()));
        
        // Kiểm tra xem mẫu xe có tồn tại không
        MauXe mauXe = mauXeRepository.findById(cauHinhTuyChinhDTO.getIdMau())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + cauHinhTuyChinhDTO.getIdMau()));
        
        // Kiểm tra xem màu sắc có tồn tại không
        MauSac mauSac = mauSacRepository.findById(cauHinhTuyChinhDTO.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + cauHinhTuyChinhDTO.getIdMauSac()));
        
        CauHinhTuyChiNh cauHinhTuyChiNh = mapToEntity(cauHinhTuyChinhDTO);
        
        // Đặt tên mặc định nếu không có
        if (cauHinhTuyChiNh.getTen() == null || cauHinhTuyChiNh.getTen().trim().isEmpty()) {
            String tenMacDinh = mauXe.getTenMau() + " - " + 
                    mauSac.getTen() + " - " + 
                    "Cấu hình #" + (cauHinhTuyChinhRepository.findByNguoiDungId(cauHinhTuyChinhDTO.getIdNguoiDung()).size() + 1);
            cauHinhTuyChiNh.setTen(tenMacDinh);
        }
        
        log.info("Đang lưu cấu hình với tên: {}", cauHinhTuyChiNh.getTen());
        
        CauHinhTuyChiNh savedCauHinh = cauHinhTuyChinhRepository.save(cauHinhTuyChiNh);
        log.info("Đã lưu cấu hình thành công với ID: {}", savedCauHinh.getId());
        
        return mapToDTO(savedCauHinh);
    }
    
    public CauHinhTuyChinhDTO updateCauHinhTuyChinh(Long id, CauHinhTuyChinhDTO cauHinhTuyChinhDTO) {
        CauHinhTuyChiNh existingCauHinh = cauHinhTuyChinhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình tùy chỉnh với ID: " + id));
        
        // Chỉ cho phép chủ sở hữu cập nhật
        if (!existingCauHinh.getNguoiDung().getId().equals(cauHinhTuyChinhDTO.getIdNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền cập nhật cấu hình này");
        }
        
        // Cập nhật thông tin
        updateEntityFromDTO(existingCauHinh, cauHinhTuyChinhDTO);
        
        // Cập nhật tổng giá nếu có thay đổi về mẫu xe, màu sắc hoặc tùy chọn
        if (cauHinhTuyChinhDTO.getIdMau() != null || 
                cauHinhTuyChinhDTO.getIdMauSac() != null || 
                cauHinhTuyChinhDTO.getDanhSachIdTuyChon() != null) {
            BigDecimal tongGia = tinhTongGia(
                    existingCauHinh.getMauXe().getId(),
                    existingCauHinh.getMauSac().getId(),
                    existingCauHinh.getDanhSachTuyChon().stream().map(TuyChon::getId).collect(Collectors.toList()));
            existingCauHinh.setTongGia(tongGia);
        }
        
        return mapToDTO(cauHinhTuyChinhRepository.save(existingCauHinh));
    }
    
    public void deleteCauHinhTuyChinh(Long id, Long idNguoiDung) {
        CauHinhTuyChiNh cauHinhTuyChiNh = cauHinhTuyChinhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình tùy chỉnh với ID: " + id));
        
        // Chỉ cho phép chủ sở hữu xóa
        if (!cauHinhTuyChiNh.getNguoiDung().getId().equals(idNguoiDung)) {
            throw new RuntimeException("Bạn không có quyền xóa cấu hình này");
        }
        
        // Kiểm tra xem cấu hình đã được sử dụng trong đơn hàng chưa
        if (cauHinhTuyChiNh.getDanhSachDonHang() != null && !cauHinhTuyChiNh.getDanhSachDonHang().isEmpty()) {
            throw new RuntimeException("Không thể xóa cấu hình đã được sử dụng trong đơn hàng");
        }
        
        cauHinhTuyChinhRepository.deleteById(id);
    }
    
    public BigDecimal tinhTongGia(Long idMauXe, Long idMauSac, List<Long> danhSachIdTuyChon) {
        // Lấy giá cơ bản của mẫu xe
        MauXe mauXe = mauXeRepository.findById(idMauXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + idMauXe));
        BigDecimal tongGia = mauXe.getGiaCoban();
        
        // Cộng thêm giá màu sắc nếu có
        MauSac mauSac = mauSacRepository.findById(idMauSac)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + idMauSac));
        if (mauSac.getGiaThem() != null) {
            tongGia = tongGia.add(mauSac.getGiaThem());
        }
        
        // Cộng thêm giá các tùy chọn
        if (danhSachIdTuyChon != null && !danhSachIdTuyChon.isEmpty()) {
            for (Long idTuyChon : danhSachIdTuyChon) {
                TuyChon tuyChon = tuyChonRepository.findById(idTuyChon)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tùy chọn với ID: " + idTuyChon));
                if (tuyChon.getGia() != null) {
                    tongGia = tongGia.add(tuyChon.getGia());
                }
            }
        }
        
        return tongGia;
    }
    
    private CauHinhTuyChiNh mapToEntity(CauHinhTuyChinhDTO dto) {
        CauHinhTuyChiNh cauHinhTuyChiNh = new CauHinhTuyChiNh();
        
        if (dto.getId() != null) {
            cauHinhTuyChiNh.setId(dto.getId());
        }
        
        // Set NguoiDung
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        cauHinhTuyChiNh.setNguoiDung(nguoiDung);
        
        // Set MauXe
        MauXe mauXe = mauXeRepository.findById(dto.getIdMau())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + dto.getIdMau()));
        cauHinhTuyChiNh.setMauXe(mauXe);
        
        // Set MauSac
        MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + dto.getIdMauSac()));
        cauHinhTuyChiNh.setMauSac(mauSac);
        
        // Set NoiThat nếu có
        if (dto.getIdNoiThat() != null) {
            NoiThat noiThat = noiThatRepository.findById(dto.getIdNoiThat())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nội thất với ID: " + dto.getIdNoiThat()));
            cauHinhTuyChiNh.setNoiThat(noiThat);
        }
        
        // Set BanhXe nếu có
        if (dto.getIdBanhXe() != null) {
            BanhXe banhXe = banhXeRepository.findById(dto.getIdBanhXe())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bánh xe với ID: " + dto.getIdBanhXe()));
            cauHinhTuyChiNh.setBanhXe(banhXe);
        }
        
        // Set các thông tin khác
        cauHinhTuyChiNh.setTen(dto.getTen());
        cauHinhTuyChiNh.setTongGia(dto.getTongGia());
        
        // Set danh sách tùy chọn
        List<TuyChon> danhSachTuyChon = new ArrayList<>();
        if (dto.getDanhSachIdTuyChon() != null && !dto.getDanhSachIdTuyChon().isEmpty()) {
            for (Long idTuyChon : dto.getDanhSachIdTuyChon()) {
                TuyChon tuyChon = tuyChonRepository.findById(idTuyChon)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tùy chọn với ID: " + idTuyChon));
                danhSachTuyChon.add(tuyChon);
            }
        }
        cauHinhTuyChiNh.setDanhSachTuyChon(danhSachTuyChon);
        
        return cauHinhTuyChiNh;
    }
    
    private void updateEntityFromDTO(CauHinhTuyChiNh cauHinhTuyChiNh, CauHinhTuyChinhDTO dto) {
        // Không đổi người dùng
        
        // Cập nhật MauXe nếu có thay đổi
        if (dto.getIdMau() != null && !dto.getIdMau().equals(cauHinhTuyChiNh.getMauXe().getId())) {
            MauXe mauXe = mauXeRepository.findById(dto.getIdMau())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + dto.getIdMau()));
            cauHinhTuyChiNh.setMauXe(mauXe);
        }
        
        // Cập nhật MauSac nếu có thay đổi
        if (dto.getIdMauSac() != null && !dto.getIdMauSac().equals(cauHinhTuyChiNh.getMauSac().getId())) {
            MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + dto.getIdMauSac()));
            cauHinhTuyChiNh.setMauSac(mauSac);
        }
        
        // Cập nhật NoiThat nếu có thay đổi
        if (dto.getIdNoiThat() != null && !dto.getIdNoiThat().equals(cauHinhTuyChiNh.getNoiThat().getId())) {
            NoiThat noiThat = noiThatRepository.findById(dto.getIdNoiThat())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nội thất với ID: " + dto.getIdNoiThat()));
            cauHinhTuyChiNh.setNoiThat(noiThat);
        }
        
        // Cập nhật BanhXe nếu có thay đổi
        if (dto.getIdBanhXe() != null && !dto.getIdBanhXe().equals(cauHinhTuyChiNh.getBanhXe().getId())) {
            BanhXe banhXe = banhXeRepository.findById(dto.getIdBanhXe())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bánh xe với ID: " + dto.getIdBanhXe()));
            cauHinhTuyChiNh.setBanhXe(banhXe);
        }
        
        // Cập nhật tên nếu có
        if (dto.getTen() != null) {
            cauHinhTuyChiNh.setTen(dto.getTen());
        }
        
        // Cập nhật tổng giá nếu có
        if (dto.getTongGia() != null) {
            cauHinhTuyChiNh.setTongGia(dto.getTongGia());
        }
        
        // Cập nhật danh sách tùy chọn nếu có
        if (dto.getDanhSachIdTuyChon() != null) {
            List<TuyChon> danhSachTuyChon = new ArrayList<>();
            for (Long idTuyChon : dto.getDanhSachIdTuyChon()) {
                TuyChon tuyChon = tuyChonRepository.findById(idTuyChon)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tùy chọn với ID: " + idTuyChon));
                danhSachTuyChon.add(tuyChon);
            }
            cauHinhTuyChiNh.setDanhSachTuyChon(danhSachTuyChon);
        }
    }
    
    private CauHinhTuyChinhDTO mapToDTO(CauHinhTuyChiNh cauHinhTuyChiNh) {
        CauHinhTuyChinhDTO dto = new CauHinhTuyChinhDTO();
        
        dto.setId(cauHinhTuyChiNh.getId());
        
        // Thông tin người dùng
        if (cauHinhTuyChiNh.getNguoiDung() != null) {
            dto.setIdNguoiDung(cauHinhTuyChiNh.getNguoiDung().getId());
            // Kết hợp họ và tên
            String ho = cauHinhTuyChiNh.getNguoiDung().getHo() != null ? cauHinhTuyChiNh.getNguoiDung().getHo() : "";
            String ten = cauHinhTuyChiNh.getNguoiDung().getTen() != null ? cauHinhTuyChiNh.getNguoiDung().getTen() : "";
            dto.setTenNguoiDung((ho + " " + ten).trim());
        }
        
        // Thông tin mẫu xe
        if (cauHinhTuyChiNh.getMauXe() != null) {
            dto.setIdMau(cauHinhTuyChiNh.getMauXe().getId());
            dto.setTenMau(cauHinhTuyChiNh.getMauXe().getTenMau());
        }
        
        // Thông tin màu sắc
        if (cauHinhTuyChiNh.getMauSac() != null) {
            dto.setIdMauSac(cauHinhTuyChiNh.getMauSac().getId());
            dto.setTenMauSac(cauHinhTuyChiNh.getMauSac().getTen());
        }
        
        // Thông tin nội thất
        if (cauHinhTuyChiNh.getNoiThat() != null) {
            dto.setIdNoiThat(cauHinhTuyChiNh.getNoiThat().getId());
            dto.setTenNoiThat(cauHinhTuyChiNh.getNoiThat().getTen());
            dto.setGiaNoiThat(cauHinhTuyChiNh.getNoiThat().getGiaThem());
        }
        
        // Thông tin bánh xe
        if (cauHinhTuyChiNh.getBanhXe() != null) {
            dto.setIdBanhXe(cauHinhTuyChiNh.getBanhXe().getId());
            dto.setTenBanhXe(cauHinhTuyChiNh.getBanhXe().getTen());
            dto.setGiaBanhXe(cauHinhTuyChiNh.getBanhXe().getGiaThem());
        }
        
        dto.setTen(cauHinhTuyChiNh.getTen());
        dto.setTongGia(cauHinhTuyChiNh.getTongGia());
        
        // Thêm danh sách tùy chọn
        if (cauHinhTuyChiNh.getDanhSachTuyChon() != null && !cauHinhTuyChiNh.getDanhSachTuyChon().isEmpty()) {
            dto.setDanhSachIdTuyChon(
                cauHinhTuyChiNh.getDanhSachTuyChon().stream()
                    .map(TuyChon::getId)
                    .collect(Collectors.toList())
            );
            
            // Map danh sách tùy chọn chi tiết
            List<TuyChonDTO> tuyChonDTOs = cauHinhTuyChiNh.getDanhSachTuyChon().stream()
                .map(tuyChon -> {
                    TuyChonDTO tuyChonDTO = new TuyChonDTO();
                    tuyChonDTO.setId(tuyChon.getId());
                    tuyChonDTO.setTen(tuyChon.getTen());
                    tuyChonDTO.setGia(tuyChon.getGia());
                    // Chuyển đổi enum DanhMuc sang String
                    tuyChonDTO.setDanhMuc(tuyChon.getDanhMuc().name());
                    return tuyChonDTO;

                })
                .collect(Collectors.toList());
            dto.setDanhSachTuyChon(tuyChonDTOs);
        }
        
        // Thời gian
        if (cauHinhTuyChiNh.getNgayTao() != null) {
            dto.setNgayTao(cauHinhTuyChiNh.getNgayTao().format(DATETIME_FORMATTER));
        }
        if (cauHinhTuyChiNh.getNgayCapNhat() != null) {
            dto.setNgayCapNhat(cauHinhTuyChiNh.getNgayCapNhat().format(DATETIME_FORMATTER));
        }
        
        // Danh sách tùy chọn
        if (cauHinhTuyChiNh.getDanhSachTuyChon() != null && !cauHinhTuyChiNh.getDanhSachTuyChon().isEmpty()) {
            dto.setDanhSachIdTuyChon(
                    cauHinhTuyChiNh.getDanhSachTuyChon().stream()
                            .map(TuyChon::getId)
                            .collect(Collectors.toList())
            );
            
            dto.setDanhSachTuyChon(
                    cauHinhTuyChiNh.getDanhSachTuyChon().stream()
                            .map(this::mapTuyChonToDTO)
                            .collect(Collectors.toList())
            );
        }
        
        // Khuyến mãi - để null vì không có trong model
        dto.setKhuyenMai(null);
        dto.setGiaSauKhuyenMai(null);
        
        return dto;
    }
    
    private TuyChonDTO mapTuyChonToDTO(TuyChon tuyChon) {
        TuyChonDTO dto = new TuyChonDTO();
        dto.setId(tuyChon.getId());
        dto.setTen(tuyChon.getTen());
        dto.setMoTa(tuyChon.getMoTa());
        
        // Chuyển enum thành String
        if (tuyChon.getDanhMuc() != null) {
            dto.setDanhMuc(tuyChon.getDanhMuc().name());
        }
        
        dto.setGia(tuyChon.getGia());
        return dto;
    }

    public byte[] xuatPDF(CauHinhTuyChinhDTO cauHinh) throws Exception {
        log.info("Bắt đầu tạo PDF cho cấu hình: {}", cauHinh.getTen());
        
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Tạo font Unicode chuẩn để hỗ trợ tiếng Việt hoàn chỉnh
            BaseFont baseFont = createVietnameseFont();
            
            // Tạo các font với encoding Unicode
            Font titleFont = new Font(baseFont, 24, Font.BOLD);
            Font headerFont = new Font(baseFont, 14, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font priceFont = new Font(baseFont, 12, Font.BOLD);
            Font totalFont = new Font(baseFont, 16, Font.BOLD);
            
            // Thêm tiêu đề
            Paragraph title = new Paragraph("CẤU HÌNH XE AUDI", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Thông tin người dùng
            if (cauHinh.getIdNguoiDung() != null) {
                Paragraph userInfoTitle = new Paragraph("THÔNG TIN NGƯỜI DÙNG", headerFont);
                userInfoTitle.setSpacingAfter(10);
                document.add(userInfoTitle);
                
                // Lấy thông tin chi tiết người dùng từ database
                NguoiDung nguoiDung = null;
                try {
                    nguoiDung = nguoiDungRepository.findById(cauHinh.getIdNguoiDung())
                            .orElse(null);
                } catch (Exception e) {
                    log.warn("Không thể lấy thông tin người dùng: {}", e.getMessage());
                }
                
                PdfPTable userTable = new PdfPTable(2);
                userTable.setWidthPercentage(100);
                userTable.setSpacingBefore(5);
                userTable.setSpacingAfter(15);
                
                // ID người dùng
                PdfPCell userIdLabel = new PdfPCell(new Phrase("ID người dùng:", headerFont));
                userIdLabel.setBorder(PdfPCell.NO_BORDER);
                userIdLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                PdfPCell userIdValue = new PdfPCell(new Phrase(cauHinh.getIdNguoiDung().toString(), normalFont));
                userIdValue.setBorder(PdfPCell.NO_BORDER);
                userTable.addCell(userIdLabel);
                userTable.addCell(userIdValue);
                
                // Tên người dùng
                String tenNguoiDung = "";
                if (nguoiDung != null) {
                    String ho = nguoiDung.getHo() != null ? nguoiDung.getHo() : "";
                    String ten = nguoiDung.getTen() != null ? nguoiDung.getTen() : "";
                    tenNguoiDung = (ho + " " + ten).trim();
                } else if (cauHinh.getTenNguoiDung() != null && !cauHinh.getTenNguoiDung().trim().isEmpty()) {
                    tenNguoiDung = cauHinh.getTenNguoiDung();
                }
                
                if (!tenNguoiDung.isEmpty()) {
                    PdfPCell userNameLabel = new PdfPCell(new Phrase("Họ và tên:", headerFont));
                    userNameLabel.setBorder(PdfPCell.NO_BORDER);
                    userNameLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    PdfPCell userNameValue = new PdfPCell(new Phrase(tenNguoiDung, normalFont));
                    userNameValue.setBorder(PdfPCell.NO_BORDER);
                    userTable.addCell(userNameLabel);
                    userTable.addCell(userNameValue);
                }
                
                // Email người dùng
                if (nguoiDung != null && nguoiDung.getEmail() != null && !nguoiDung.getEmail().trim().isEmpty()) {
                    PdfPCell emailLabel = new PdfPCell(new Phrase("Email:", headerFont));
                    emailLabel.setBorder(PdfPCell.NO_BORDER);
                    emailLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    PdfPCell emailValue = new PdfPCell(new Phrase(nguoiDung.getEmail(), normalFont));
                    emailValue.setBorder(PdfPCell.NO_BORDER);
                    userTable.addCell(emailLabel);
                    userTable.addCell(emailValue);
                }
                
                // Số điện thoại
                if (nguoiDung != null && nguoiDung.getSoDienThoai() != null && !nguoiDung.getSoDienThoai().trim().isEmpty()) {
                    PdfPCell phoneLabel = new PdfPCell(new Phrase("Số điện thoại:", headerFont));
                    phoneLabel.setBorder(PdfPCell.NO_BORDER);
                    phoneLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    PdfPCell phoneValue = new PdfPCell(new Phrase(nguoiDung.getSoDienThoai(), normalFont));
                    phoneValue.setBorder(PdfPCell.NO_BORDER);
                    userTable.addCell(phoneLabel);
                    userTable.addCell(phoneValue);
                }
                
                // Ngày tạo cấu hình
                if (cauHinh.getNgayTao() != null && !cauHinh.getNgayTao().trim().isEmpty()) {
                    PdfPCell dateLabel = new PdfPCell(new Phrase("Ngày tạo cấu hình:", headerFont));
                    dateLabel.setBorder(PdfPCell.NO_BORDER);
                    dateLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    PdfPCell dateValue = new PdfPCell(new Phrase(cauHinh.getNgayTao(), normalFont));
                    dateValue.setBorder(PdfPCell.NO_BORDER);
                    userTable.addCell(dateLabel);
                    userTable.addCell(dateValue);
                }
                
                // Ngày cập nhật (nếu có)
                if (cauHinh.getNgayCapNhat() != null && !cauHinh.getNgayCapNhat().trim().isEmpty()) {
                    PdfPCell updateDateLabel = new PdfPCell(new Phrase("Ngày cập nhật:", headerFont));
                    updateDateLabel.setBorder(PdfPCell.NO_BORDER);
                    updateDateLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    PdfPCell updateDateValue = new PdfPCell(new Phrase(cauHinh.getNgayCapNhat(), normalFont));
                    updateDateValue.setBorder(PdfPCell.NO_BORDER);
                    userTable.addCell(updateDateLabel);
                    userTable.addCell(updateDateValue);
                }
                
                // Tên cấu hình
                if (cauHinh.getTen() != null && !cauHinh.getTen().trim().isEmpty()) {
                    PdfPCell configNameLabel = new PdfPCell(new Phrase("Tên cấu hình:", headerFont));
                    configNameLabel.setBorder(PdfPCell.NO_BORDER);
                    configNameLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    PdfPCell configNameValue = new PdfPCell(new Phrase(cauHinh.getTen(), normalFont));
                    configNameValue.setBorder(PdfPCell.NO_BORDER);
                    userTable.addCell(configNameLabel);
                    userTable.addCell(configNameValue);
                }
                
                document.add(userTable);
            }
            
            // Tạo bảng thông tin chính
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(20);
            mainTable.setSpacingAfter(20);
            
            // Mẫu xe
            if (cauHinh.getTenMau() != null) {
                PdfPCell labelCell = new PdfPCell(new Phrase("Mẫu xe:", headerFont));
                labelCell.setBorder(PdfPCell.NO_BORDER);
                labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                
                PdfPCell valueCell = new PdfPCell(new Phrase(cauHinh.getTenMau(), normalFont));
                valueCell.setBorder(PdfPCell.NO_BORDER);
                
                mainTable.addCell(labelCell);
                mainTable.addCell(valueCell);
            }
            
            // Màu sắc
            if (cauHinh.getTenMauSac() != null) {
                PdfPCell labelCell = new PdfPCell(new Phrase("Màu sắc:", headerFont));
                labelCell.setBorder(PdfPCell.NO_BORDER);
                labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                
                Paragraph colorInfo = new Paragraph();
                colorInfo.add(new Chunk(cauHinh.getTenMauSac(), normalFont));
                
                // Tìm giá thêm của màu sắc
                try {
                    MauSac mauSac = mauSacRepository.findById(cauHinh.getIdMauSac())
                            .orElse(null);
                    if (mauSac != null && mauSac.getGiaThem() != null && mauSac.getGiaThem().compareTo(BigDecimal.ZERO) > 0) {
                        colorInfo.add(new Chunk(" (+" + formatCurrency(mauSac.getGiaThem()) + ")", priceFont));
                    }
                } catch (Exception e) {
                    log.warn("Không thể lấy thông tin màu sắc: {}", e.getMessage());
                }
                
                PdfPCell valueCell = new PdfPCell(colorInfo);
                valueCell.setBorder(PdfPCell.NO_BORDER);
                
                mainTable.addCell(labelCell);
                mainTable.addCell(valueCell);
            }
            
            // Nội thất
            if (cauHinh.getTenNoiThat() != null) {
                PdfPCell labelCell = new PdfPCell(new Phrase("Nội thất:", headerFont));
                labelCell.setBorder(PdfPCell.NO_BORDER);
                labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                
                Paragraph interiorInfo = new Paragraph();
                interiorInfo.add(new Chunk(cauHinh.getTenNoiThat(), normalFont));
                
                if (cauHinh.getGiaNoiThat() != null && cauHinh.getGiaNoiThat().compareTo(BigDecimal.ZERO) > 0) {
                    interiorInfo.add(new Chunk(" (+" + formatCurrency(cauHinh.getGiaNoiThat()) + ")", priceFont));
                }
                
                PdfPCell valueCell = new PdfPCell(interiorInfo);
                valueCell.setBorder(PdfPCell.NO_BORDER);
                
                mainTable.addCell(labelCell);
                mainTable.addCell(valueCell);
            }
            
            // Bánh xe
            if (cauHinh.getTenBanhXe() != null) {
                PdfPCell labelCell = new PdfPCell(new Phrase("Bánh xe:", headerFont));
                labelCell.setBorder(PdfPCell.NO_BORDER);
                labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                
                Paragraph wheelInfo = new Paragraph();
                wheelInfo.add(new Chunk(cauHinh.getTenBanhXe(), normalFont));
                
                if (cauHinh.getGiaBanhXe() != null && cauHinh.getGiaBanhXe().compareTo(BigDecimal.ZERO) > 0) {
                    wheelInfo.add(new Chunk(" (+" + formatCurrency(cauHinh.getGiaBanhXe()) + ")", priceFont));
                }
                
                PdfPCell valueCell = new PdfPCell(wheelInfo);
                valueCell.setBorder(PdfPCell.NO_BORDER);
                
                mainTable.addCell(labelCell);
                mainTable.addCell(valueCell);
            }
            
            document.add(mainTable);
            
            // Tùy chọn
            if (cauHinh.getDanhSachTuyChon() != null && !cauHinh.getDanhSachTuyChon().isEmpty()) {
                document.add(new Paragraph(""));
                document.add(new Paragraph("TÙY CHỌN ĐÃ CHỌN:", headerFont));
                
                PdfPTable optionsTable = new PdfPTable(3);
                optionsTable.setWidthPercentage(100);
                optionsTable.setSpacingBefore(10);
                optionsTable.setSpacingAfter(20);
                
                // Header
                PdfPCell header1 = new PdfPCell(new Phrase("Tên tùy chọn", headerFont));
                PdfPCell header2 = new PdfPCell(new Phrase("Danh mục", headerFont));
                PdfPCell header3 = new PdfPCell(new Phrase("Giá", headerFont));
                header1.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header2.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header3.setBackgroundColor(BaseColor.LIGHT_GRAY);
                optionsTable.addCell(header1);
                optionsTable.addCell(header2);
                optionsTable.addCell(header3);
                
                for (TuyChonDTO tuyChon : cauHinh.getDanhSachTuyChon()) {
                    optionsTable.addCell(new Phrase(tuyChon.getTen(), normalFont));
                    optionsTable.addCell(new Phrase(tuyChon.getDanhMuc(), normalFont));
                    optionsTable.addCell(new Phrase("+" + formatCurrency(tuyChon.getGia()), priceFont));
                }
                
                document.add(optionsTable);
            }
            
            // Thông tin giá chi tiết
            document.add(new Paragraph(""));
            document.add(new Paragraph("CHI TIẾT GIÁ:", headerFont));
            
            PdfPTable priceTable = new PdfPTable(2);
            priceTable.setWidthPercentage(100);
            priceTable.setSpacingBefore(10);
            priceTable.setSpacingAfter(20);
            
            // Giá cơ bản
            if (cauHinh.getTenMau() != null) {
                try {
                    MauXe mauXe = mauXeRepository.findById(cauHinh.getIdMau())
                            .orElse(null);
                    if (mauXe != null && mauXe.getGiaCoban() != null) {
                        priceTable.addCell(new Phrase("Giá cơ bản:", normalFont));
                        priceTable.addCell(new Phrase(formatCurrency(mauXe.getGiaCoban()), priceFont));
                    }
                } catch (Exception e) {
                    log.warn("Không thể lấy giá cơ bản: {}", e.getMessage());
                }
            }
            
            // Giá màu sắc
            try {
                MauSac mauSac = mauSacRepository.findById(cauHinh.getIdMauSac())
                        .orElse(null);
                if (mauSac != null && mauSac.getGiaThem() != null && mauSac.getGiaThem().compareTo(BigDecimal.ZERO) > 0) {
                    priceTable.addCell(new Phrase("Giá màu sắc:", normalFont));
                    priceTable.addCell(new Phrase("+" + formatCurrency(mauSac.getGiaThem()), priceFont));
                }
            } catch (Exception e) {
                log.warn("Không thể lấy giá màu sắc: {}", e.getMessage());
            }
            
            // Giá nội thất
            if (cauHinh.getGiaNoiThat() != null && cauHinh.getGiaNoiThat().compareTo(BigDecimal.ZERO) > 0) {
                priceTable.addCell(new Phrase("Giá nội thất:", normalFont));
                priceTable.addCell(new Phrase("+" + formatCurrency(cauHinh.getGiaNoiThat()), priceFont));
            }
            
            // Giá bánh xe
            if (cauHinh.getGiaBanhXe() != null && cauHinh.getGiaBanhXe().compareTo(BigDecimal.ZERO) > 0) {
                priceTable.addCell(new Phrase("Giá bánh xe:", normalFont));
                priceTable.addCell(new Phrase("+" + formatCurrency(cauHinh.getGiaBanhXe()), priceFont));
            }
            
            // Tổng giá tùy chọn
            if (cauHinh.getDanhSachTuyChon() != null && !cauHinh.getDanhSachTuyChon().isEmpty()) {
                BigDecimal tongGiaTuyChon = cauHinh.getDanhSachTuyChon().stream()
                        .map(tuyChon -> tuyChon.getGia() != null ? tuyChon.getGia() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                if (tongGiaTuyChon.compareTo(BigDecimal.ZERO) > 0) {
                    priceTable.addCell(new Phrase("Tổng giá tùy chọn:", normalFont));
                    priceTable.addCell(new Phrase("+" + formatCurrency(tongGiaTuyChon), priceFont));
                }
            }
            
            document.add(priceTable);
            
            // Tổng giá
            document.add(new Paragraph(""));
            Paragraph totalInfo = new Paragraph();
            totalInfo.add(new Chunk("TỔNG GIÁ: ", totalFont));
            totalInfo.add(new Chunk(formatCurrency(cauHinh.getTongGia()), totalFont));
            totalInfo.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalInfo);
            
            // Footer
            document.add(new Paragraph(""));
            Paragraph footer = new Paragraph("Ngày xuất: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            log.info("Đã tạo PDF thành công, kích thước: {} bytes", baos.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo PDF: ", e);
            throw new RuntimeException("Không thể tạo PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo font hỗ trợ tiếng Việt hoàn chỉnh từ resource
     */
    private BaseFont createVietnameseFont() throws Exception {
        try {
            // Cách 1: Sử dụng font Arial Unicode từ resource (nếu có)
            InputStream fontStream = getClass().getResourceAsStream("/fonts/arial-unicode.ttf");
            if (fontStream != null) {
                log.info("Sử dụng font Arial Unicode từ resource");
                return BaseFont.createFont("/fonts/arial-unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        } catch (Exception e) {
            log.warn("Không thể tạo font Arial Unicode từ resource: {}", e.getMessage());
        }
        
        try {
            // Cách 2: Sử dụng font Times New Roman từ resource (nếu có)
            InputStream fontStream = getClass().getResourceAsStream("/fonts/times-new-roman.ttf");
            if (fontStream != null) {
                log.info("Sử dụng font Times New Roman từ resource");
                return BaseFont.createFont("/fonts/times-new-roman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        } catch (Exception e) {
            log.warn("Không thể tạo font Times New Roman từ resource: {}", e.getMessage());
        }
        
        try {
            // Cách 3: Sử dụng font Arial Unicode từ hệ thống macOS
            String[] fontPaths = {
                "/System/Library/Fonts/Supplemental/Arial Unicode.ttf", // macOS
                "/System/Library/Fonts/Supplemental/Times New Roman.ttf", // macOS
                "C:/Windows/Fonts/arial.ttf", // Windows
                "C:/Windows/Fonts/times.ttf", // Windows
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", // Linux
                "/usr/share/fonts/TTF/arial.ttf" // Linux
            };
            
            for (String fontPath : fontPaths) {
                try {
                    File fontFile = new File(fontPath);
                    if (fontFile.exists()) {
                        log.info("Sử dụng font từ hệ thống: {}", fontPath);
                        return BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    }
                } catch (Exception e) {
                    log.debug("Không thể sử dụng font {}: {}", fontPath, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Không thể tạo font từ hệ thống: {}", e.getMessage());
        }
        
        try {
            // Cách 4: Sử dụng font mặc định với encoding Unicode
            log.info("Sử dụng font mặc định với encoding Unicode");
            return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            log.warn("Không thể tạo font Unicode: {}", e.getMessage());
            
            try {
                // Cách 5: Sử dụng font mặc định với encoding CP1252
                log.info("Sử dụng font mặc định với encoding CP1252");
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception e2) {
                log.error("Không thể tạo font nào: {}", e2.getMessage());
                throw new RuntimeException("Không thể tạo font để tạo PDF", e2);
            }
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
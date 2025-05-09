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

@Service
@Transactional
public class CauHinhTuyChinhService {

    private final CauHinhTuyChinhRepository cauHinhTuyChinhRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final MauXeRepository mauXeRepository;
    private final MauSacRepository mauSacRepository;
    private final TuyChonRepository tuyChonRepository;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CauHinhTuyChinhService(
            CauHinhTuyChinhRepository cauHinhTuyChinhRepository,
            NguoiDungRepository nguoiDungRepository,
            MauXeRepository mauXeRepository,
            MauSacRepository mauSacRepository,
            TuyChonRepository tuyChonRepository) {
        this.cauHinhTuyChinhRepository = cauHinhTuyChinhRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.mauXeRepository = mauXeRepository;
        this.mauSacRepository = mauSacRepository;
        this.tuyChonRepository = tuyChonRepository;
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
        return cauHinhTuyChinhRepository.findByNguoiDungId(idNguoiDung).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CauHinhTuyChinhDTO> getCauHinhTuyChinhByMauXe(Long idMauXe) {
        return cauHinhTuyChinhRepository.findByMauXeId(idMauXe).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public CauHinhTuyChinhDTO createCauHinhTuyChinh(CauHinhTuyChinhDTO cauHinhTuyChinhDTO) {
        // Kiểm tra dữ liệu đầu vào
        if (cauHinhTuyChinhDTO.getIdMau() == null || cauHinhTuyChinhDTO.getIdMauSac() == null) {
            throw new RuntimeException("Phải chọn mẫu xe và màu sắc");
        }
        
        // Tính tổng giá nếu không được cung cấp
        if (cauHinhTuyChinhDTO.getTongGia() == null) {
            BigDecimal tongGia = tinhTongGia(
                    cauHinhTuyChinhDTO.getIdMau(), 
                    cauHinhTuyChinhDTO.getIdMauSac(), 
                    cauHinhTuyChinhDTO.getDanhSachIdTuyChon());
            cauHinhTuyChinhDTO.setTongGia(tongGia);
        }
        
        CauHinhTuyChiNh cauHinhTuyChiNh = mapToEntity(cauHinhTuyChinhDTO);
        
        // Đặt tên mặc định nếu không có
        if (cauHinhTuyChiNh.getTen() == null || cauHinhTuyChiNh.getTen().trim().isEmpty()) {
            String tenMacDinh = cauHinhTuyChiNh.getMauXe().getTenMau() + " - " + 
                    cauHinhTuyChiNh.getMauSac().getTen() + " - " + 
                    "Cấu hình #" + (cauHinhTuyChinhRepository.findByNguoiDungId(cauHinhTuyChinhDTO.getIdNguoiDung()).size() + 1);
            cauHinhTuyChiNh.setTen(tenMacDinh);
        }
        
        return mapToDTO(cauHinhTuyChinhRepository.save(cauHinhTuyChiNh));
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
        
        // NguoiDung info
        if (cauHinhTuyChiNh.getNguoiDung() != null) {
            dto.setIdNguoiDung(cauHinhTuyChiNh.getNguoiDung().getId());
            dto.setTenNguoiDung(cauHinhTuyChiNh.getNguoiDung().getHo() + " " + cauHinhTuyChiNh.getNguoiDung().getTen());
        }
        
        // MauXe info
        if (cauHinhTuyChiNh.getMauXe() != null) {
            dto.setIdMau(cauHinhTuyChiNh.getMauXe().getId());
            dto.setTenMau(cauHinhTuyChiNh.getMauXe().getTenMau());
        }
        
        // MauSac info
        if (cauHinhTuyChiNh.getMauSac() != null) {
            dto.setIdMauSac(cauHinhTuyChiNh.getMauSac().getId());
            dto.setTenMauSac(cauHinhTuyChiNh.getMauSac().getTen());
        }
        
        // Các thông tin khác
        dto.setTen(cauHinhTuyChiNh.getTen());
        dto.setTongGia(cauHinhTuyChiNh.getTongGia());
        
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
}
package com.audistore.audi.service;

import com.audistore.audi.dto.NguoiDungDTO;
import com.audistore.audi.dto.NguoiDungQuyenDTO;
import com.audistore.audi.dto.NhomQuyenDTO;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.model.NhomQuyen;
import com.audistore.audi.model.MatKhauNguoiDung;
import com.audistore.audi.model.Quyen;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.repository.NhomQuyenRepository;
import com.audistore.audi.repository.MatKhauNguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional
public class NguoiDungService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NhomQuyenRepository nhomQuyenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NhomQuyenService nhomQuyenService;
    private final MatKhauNguoiDungRepository matKhauNguoiDungRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public NguoiDungService(NguoiDungRepository nguoiDungRepository, NhomQuyenRepository nhomQuyenRepository, PasswordEncoder passwordEncoder, NhomQuyenService nhomQuyenService, MatKhauNguoiDungRepository matKhauNguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.nhomQuyenRepository = nhomQuyenRepository;
        this.passwordEncoder = passwordEncoder;
        this.nhomQuyenService = nhomQuyenService;
        this.matKhauNguoiDungRepository = matKhauNguoiDungRepository;
    }

    public List<NguoiDungDTO> getAllNguoiDung() {
        return nguoiDungRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NguoiDungDTO getNguoiDungById(Long id) {
        return nguoiDungRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với ID: " + id));
    }

    public NguoiDungDTO createNguoiDung(NguoiDungDTO nguoiDungDTO) {
        NguoiDung nguoiDung = mapToEntity(nguoiDungDTO);
        NguoiDung savedNguoiDung = nguoiDungRepository.save(nguoiDung);
        
        // Lưu mật khẩu gốc
        if (nguoiDungDTO.getMatKhau() != null) {
            MatKhauNguoiDung matKhauNguoiDung = new MatKhauNguoiDung();
            matKhauNguoiDung.setIdNguoiDung(savedNguoiDung.getId());
            matKhauNguoiDung.setMatKhauGoc(nguoiDungDTO.getMatKhau());
            matKhauNguoiDung.setNgayCapNhat(LocalDateTime.now());
            matKhauNguoiDungRepository.save(matKhauNguoiDung);
        }
        
        return mapToDTO(savedNguoiDung);
    }

    public NguoiDungDTO updateNguoiDung(Long id, NguoiDungDTO nguoiDungDTO) {
        NguoiDung existingNguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với ID: " + id));

        // Cập nhật thông tin
        updateEntityFromDTO(existingNguoiDung, nguoiDungDTO);

        return mapToDTO(nguoiDungRepository.save(existingNguoiDung));
    }

    public void deleteNguoiDung(Long id) {
        if (!nguoiDungRepository.existsById(id)) {
            throw new RuntimeException("Người dùng không tồn tại với ID: " + id);
        }
        nguoiDungRepository.deleteById(id);
    }

    /**
     * Gán nhóm quyền cho người dùng
     */
    public void giaoPhanQuyen(NguoiDungQuyenDTO dto) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + dto.getIdNguoiDung()));
        
        Set<NhomQuyen> nhomQuyen = nhomQuyenRepository.findAllById(dto.getIdNhomQuyen())
                .stream().collect(Collectors.toSet());
        
        nguoiDung.setNhomQuyen(nhomQuyen);
        nguoiDungRepository.save(nguoiDung);
    }

    /**
     * Lấy danh sách nhóm quyền của người dùng
     */
    public Set<NhomQuyenDTO> getNhomQuyenByNguoiDungId(Long idNguoiDung) {
        if (!nguoiDungRepository.existsById(idNguoiDung)) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + idNguoiDung);
        }
        
        return nhomQuyenRepository.findByNguoiDungId(idNguoiDung).stream()
                .map(nhomQuyenService::mapToDTO)
                .collect(Collectors.toSet());
    }

    public void resetPassword(Long id, String newPassword) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
                
        nguoiDung.setMatKhauMaHoa(passwordEncoder.encode(newPassword));
        
        nguoiDungRepository.save(nguoiDung);
    }

    // Helper methods for mapping
    private NguoiDungDTO mapToDTO(NguoiDung nguoiDung) {
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(nguoiDung.getId());
        dto.setEmail(nguoiDung.getEmail());
        
        // Thêm mật khẩu đã mã hóa vào DTO
        dto.setMatKhau(nguoiDung.getMatKhauMaHoa());
        
        dto.setHo(nguoiDung.getHo());
        dto.setTen(nguoiDung.getTen());
        dto.setSoDienThoai(nguoiDung.getSoDienThoai());
        dto.setDiaChi(nguoiDung.getDiaChi());
        dto.setThanhPho(nguoiDung.getThanhPho());
        dto.setTinh(nguoiDung.getTinh());
        dto.setMaBuuDien(nguoiDung.getMaBuuDien());
        dto.setQuocGia(nguoiDung.getQuocGia());
        dto.setVaiTro(nguoiDung.getVaiTro().name());
        dto.setTrangThai(nguoiDung.getTrangThai() != null ? nguoiDung.getTrangThai() : true);
        
        // Format ngày tạo và ngày cập nhật
        if (nguoiDung.getNgayTao() != null) {
            dto.setNgayTao(nguoiDung.getNgayTao().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        if (nguoiDung.getNgayCapNhat() != null) {
            dto.setNgayCapNhat(nguoiDung.getNgayCapNhat().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            // Nếu ngày cập nhật là null, sử dụng ngày tạo làm ngày cập nhật
            if (nguoiDung.getNgayTao() != null) {
                dto.setNgayCapNhat(nguoiDung.getNgayTao().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }
        
        return dto;
    }

    private NguoiDung mapToEntity(NguoiDungDTO dto) {
        NguoiDung nguoiDung = new NguoiDung();
        updateEntityFromDTO(nguoiDung, dto);
        return nguoiDung;
    }

    private void updateEntityFromDTO(NguoiDung nguoiDung, NguoiDungDTO dto) {
        // Skip ID as it shouldn't be updated
        
        nguoiDung.setHo(dto.getHo());
        nguoiDung.setTen(dto.getTen());
        nguoiDung.setSoDienThoai(dto.getSoDienThoai());
        nguoiDung.setDiaChi(dto.getDiaChi());
        nguoiDung.setThanhPho(dto.getThanhPho());
        nguoiDung.setTinh(dto.getTinh());
        nguoiDung.setMaBuuDien(dto.getMaBuuDien());
        nguoiDung.setQuocGia(dto.getQuocGia());
        
        if (dto.getVaiTro() != null) {
            nguoiDung.setVaiTro(NguoiDung.VaiTro.valueOf(dto.getVaiTro()));
        }
        
        if (dto.getTrangThai() != null) {
            nguoiDung.setTrangThai(dto.getTrangThai());
        }
        
        // Chỉ kiểm tra và cập nhật mật khẩu nếu trường mật khẩu tồn tại trong DTO
        try {
            String password = dto.getMatKhau();
            if (password != null && !password.isEmpty()) {
                nguoiDung.setMatKhauMaHoa(passwordEncoder.encode(password));
            }
        } catch (Exception e) {
            // Trường hợp không có trường matKhau trong DTO
            // Bỏ qua cập nhật mật khẩu
        }
    }

    public String getMatKhauGoc(Long idNguoiDung) {
        // Kiểm tra người dùng hiện tại có phải admin không
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Bạn chưa đăng nhập");
        }
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
        if (!isAdmin) {
            throw new AccessDeniedException("Bạn không có quyền xem mật khẩu gốc");
        }
        
        // For security reasons, we no longer return plaintext passwords
        return "**********";
    }

    // Sửa phương thức lưu người dùng để lưu cả mật khẩu gốc
    public NguoiDung saveNguoiDung(NguoiDungDTO nguoiDungDTO) {
        // Kiểm tra xem đây là cập nhật hay tạo mới
        NguoiDung nguoiDung;
        if (nguoiDungDTO.getId() != null) {
            // Cập nhật
            nguoiDung = nguoiDungRepository.findById(nguoiDungDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + nguoiDungDTO.getId()));
            
            // Cập nhật các trường thông tin cơ bản
            nguoiDung.setHo(nguoiDungDTO.getHo());
            nguoiDung.setTen(nguoiDungDTO.getTen());
            nguoiDung.setSoDienThoai(nguoiDungDTO.getSoDienThoai());
            nguoiDung.setDiaChi(nguoiDungDTO.getDiaChi());
            nguoiDung.setThanhPho(nguoiDungDTO.getThanhPho());
            nguoiDung.setTinh(nguoiDungDTO.getTinh());
            nguoiDung.setMaBuuDien(nguoiDungDTO.getMaBuuDien());
            nguoiDung.setQuocGia(nguoiDungDTO.getQuocGia());
            
            // Đổi cách lấy trạng thái
            nguoiDung.setTrangThai(nguoiDungDTO.getTrangThai());
            
            // Nếu có thay đổi vai trò và người dùng hiện tại có quyền
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_QUAN_TRI"));
            if (isAdmin && nguoiDungDTO.getVaiTro() != null) {
                nguoiDung.setVaiTro(NguoiDung.VaiTro.valueOf(nguoiDungDTO.getVaiTro()));
            }
        } else {
            // Tạo mới
            nguoiDung = new NguoiDung();
            nguoiDung.setEmail(nguoiDungDTO.getEmail());
            nguoiDung.setHo(nguoiDungDTO.getHo());
            nguoiDung.setTen(nguoiDungDTO.getTen());
            nguoiDung.setSoDienThoai(nguoiDungDTO.getSoDienThoai());
            nguoiDung.setDiaChi(nguoiDungDTO.getDiaChi());
            nguoiDung.setThanhPho(nguoiDungDTO.getThanhPho());
            nguoiDung.setTinh(nguoiDungDTO.getTinh());
            nguoiDung.setMaBuuDien(nguoiDungDTO.getMaBuuDien());
            nguoiDung.setQuocGia(nguoiDungDTO.getQuocGia());
            
            // Đổi cách lấy trạng thái
            nguoiDung.setTrangThai(nguoiDungDTO.getTrangThai());
            
            // Vai trò mặc định hoặc được chỉ định
            if (nguoiDungDTO.getVaiTro() != null) {
                nguoiDung.setVaiTro(NguoiDung.VaiTro.valueOf(nguoiDungDTO.getVaiTro()));
            } else {
                nguoiDung.setVaiTro(NguoiDung.VaiTro.khach_hang);
            }
        }
        
        // Nếu có mật khẩu mới
        if (nguoiDungDTO.getMatKhau() != null && !nguoiDungDTO.getMatKhau().isEmpty()) {
            String matKhauGoc = nguoiDungDTO.getMatKhau();
            String matKhauMaHoa = passwordEncoder.encode(matKhauGoc);
            
            nguoiDung.setMatKhauMaHoa(matKhauMaHoa);
        }
        
        return nguoiDungRepository.save(nguoiDung);
    }

    private NguoiDung getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        String username = authentication.getName();
        return nguoiDungRepository.findByEmail(username).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + username));
        
        // Tạo danh sách quyền dựa trên vai trò
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro().name().toUpperCase()));
        
        // Thêm các quyền từ nhóm quyền nếu có
        if (nguoiDung.getNhomQuyen() != null) {
            for (NhomQuyen nhomQuyen : nguoiDung.getNhomQuyen()) {
                for (Quyen quyen : nhomQuyen.getDanhSachQuyen()) {
                    authorities.add(new SimpleGrantedAuthority(quyen.getMaQuyen()));
                }
            }
        }
        
        return new User(
                nguoiDung.getEmail(),
                nguoiDung.getMatKhauMaHoa(),
                nguoiDung.getTrangThai(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}
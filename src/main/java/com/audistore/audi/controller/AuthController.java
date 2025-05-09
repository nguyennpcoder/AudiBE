package com.audistore.audi.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audistore.audi.dto.DangKyRequest;
import com.audistore.audi.dto.DangNhapRequest;
import com.audistore.audi.dto.JwtResponse;
import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.security.jwt.JwtUtils;
import com.audistore.audi.security.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    NguoiDungRepository nguoiDungRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody DangNhapRequest dangNhapRequest) {
        // Kiểm tra đăng nhập
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dangNhapRequest.getTenDangNhap(), dangNhapRequest.getMatKhau()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String vaiTro = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse("");

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getEmail(), 
                                                 vaiTro));
    }

    @PostMapping("/dang-ky")
    public ResponseEntity<?> registerUser(@Valid @RequestBody DangKyRequest dangKyRequest) {
        Map<String, String> response = new HashMap<>();
        
        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungRepository.existsByEmail(dangKyRequest.getEmail())) {
            response.put("message", "Email đã được sử dụng!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra số điện thoại đã tồn tại chưa (nếu có)
        if (dangKyRequest.getSoDienThoai() != null && !dangKyRequest.getSoDienThoai().isEmpty()
                && nguoiDungRepository.existsBySoDienThoai(dangKyRequest.getSoDienThoai())) {
            response.put("message", "Số điện thoại đã được sử dụng!");
            return ResponseEntity.badRequest().body(response);
        }

        // Tạo tài khoản người dùng mới
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail(dangKyRequest.getEmail());
        nguoiDung.setMatKhauMaHoa(encoder.encode(dangKyRequest.getMatKhau()));
        nguoiDung.setHo(dangKyRequest.getHo());
        nguoiDung.setTen(dangKyRequest.getTen());
        nguoiDung.setSoDienThoai(dangKyRequest.getSoDienThoai());
        nguoiDung.setDiaChi(dangKyRequest.getDiaChi());
        nguoiDung.setThanhPho(dangKyRequest.getThanhPho());
        nguoiDung.setTinh(dangKyRequest.getTinh());
        nguoiDung.setMaBuuDien(dangKyRequest.getMaBuuDien());
        nguoiDung.setQuocGia(dangKyRequest.getQuocGia());
        nguoiDung.setVaiTro(NguoiDung.VaiTro.khach_hang); // Mặc định là khách hàng

        nguoiDungRepository.save(nguoiDung);

        response.put("message", "Người dùng đã được đăng ký thành công!");
        return ResponseEntity.ok(response);
    }
}
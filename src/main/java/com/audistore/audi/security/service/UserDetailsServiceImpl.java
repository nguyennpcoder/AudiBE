package com.audistore.audi.security.service;

import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.repository.NguoiDungRepository;
import com.audistore.audi.repository.NhomQuyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    NguoiDungRepository nguoiDungRepository;
    
    @Autowired
    NhomQuyenRepository nhomQuyenRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Có thể là email hoặc số điện thoại
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(username)
                .orElseGet(() -> nguoiDungRepository.findBySoDienThoai(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với tên đăng nhập: " + username)));

        // KHÔNG throw exception khi user bị khóa, để AuthController xử lý
        // Chỉ load user và để isEnabled() xử lý trạng thái

        // Lấy danh sách mã quyền của người dùng
        Set<String> permissions = nhomQuyenRepository.findQuyenByNguoiDungId(nguoiDung.getId());
        
        return UserDetailsImpl.build(nguoiDung, permissions);
    }
    
    // Method để clear cache khi cập nhật user
    @CacheEvict(value = "userDetails", key = "#username")
    public void clearUserCache(String username) {
        // Method này sẽ clear cache cho user cụ thể
        System.out.println("Clearing cache for user: " + username);
    }
}
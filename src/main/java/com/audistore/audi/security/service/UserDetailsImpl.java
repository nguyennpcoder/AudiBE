package com.audistore.audi.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.audistore.audi.model.NguoiDung;
import com.audistore.audi.model.NhomQuyen;
import com.audistore.audi.model.Quyen;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String soDienThoai;
    private String ho;
    private String ten;
    private boolean trangThai;
    
    @JsonIgnore
    private String matKhau;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String soDienThoai, String ho, String ten, 
            String matKhau, boolean trangThai, String avatar, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.ho = ho;
        this.ten = ten;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.avatar = avatar;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(NguoiDung nguoiDung, Collection<String> permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Thêm vai trò như một GrantedAuthority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro().name().toUpperCase()));
        
        // Thêm các quyền từ nhóm quyền
        if (permissions != null) {
            authorities.addAll(permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission))
                    .collect(Collectors.toList()));
        }

        return new UserDetailsImpl(
                nguoiDung.getId(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getHo(),
                nguoiDung.getTen(),
                nguoiDung.getMatKhauMaHoa(),
                nguoiDung.getTrangThai(),
                nguoiDung.getAvatar(), // hoặc getAnhDaiDien(), getAnh_dai_dien() nếu DB lưu tên khác
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }
    
    public String getHo() {
        return ho;
    }
    
    public String getTen() {
        return ten;
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public String getUsername() {
        return email; // Sử dụng email làm username mặc định
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Không lock account, chỉ dựa vào trangThai
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return trangThai; // Chỉ dựa vào trangThai để quyết định user có được đăng nhập hay không
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    private String avatar; // hoặc anhDaiDien, anh_dai_dien nếu cần

    public String getAvatar() {
        return avatar;
    }
}
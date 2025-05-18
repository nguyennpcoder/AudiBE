package com.audistore.audi.configuration;

import com.audistore.audi.security.jwt.AuthEntryPointJwt;
import com.audistore.audi.security.jwt.AuthTokenFilter;
import com.audistore.audi.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth
                    // Thêm dòng này để cho phép truy cập không cần xác thực vào thư mục uploads
                     .requestMatchers("/uploads/**").permitAll()
                    .requestMatchers("/uploads/images/**").permitAll()
                    .requestMatchers("/uploads/images/vehicles/**").permitAll()
                    .requestMatchers("/uploads/images/colors/**").permitAll()
                    .requestMatchers("/uploads/images/interiors/**").permitAll()
                    
                    // Giữ nguyên các cấu hình còn lại
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/public/**").permitAll()
                     // Các API bài viết công khai
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/public").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/*/public").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/{id}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/tag/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/search/public").permitAll()
        
                    .requestMatchers(HttpMethod.GET, "/api/v1/dong-xe/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/mau-xe/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/dai-ly/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/ton-kho/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/cau-hinh/tinh-gia").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/cau-hinh/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/cau-hinh/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/cau-hinh/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/cau-hinh/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/don-hang/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/don-hang/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/don-hang/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/don-hang/**").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/thanh-toan/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/thanh-toan/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/thanh-toan/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/thanh-toan/**").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/lai-thu/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/lai-thu/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/lai-thu/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/lai-thu/**").hasAnyRole("QUAN_TRI", "BAN_HANG", "HO_TRO")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/lai-thu/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bao-duong/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/bao-duong/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/bao-duong/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/bao-duong/**").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/bao-duong/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/dong-xe/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/dong-xe/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/dong-xe/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.POST, "/api/v1/mau-xe/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/mau-xe/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/mau-xe/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.POST, "/api/v1/dai-ly/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/dai-ly/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/dai-ly/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.POST, "/api/v1/ton-kho/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/ton-kho/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/ton-kho/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/ton-kho/**").hasRole("QUAN_TRI")
                     // Quản lý bài viết (yêu cầu xác thực)
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/page").authenticated() 
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/danh-muc/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/bai-viet/search").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/bai-viet/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/bai-viet/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/bai-viet/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/bai-viet/**").authenticated()
        
                    // Cấu hình quyền cho API Đánh giá sản phẩm
                    .requestMatchers(HttpMethod.GET, "/api/v1/danh-gia/mau-xe/*/trung-binh").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/danh-gia/mau-xe/*").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/danh-gia").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/danh-gia/nguoi-dung/*").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/danh-gia/*").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/danh-gia").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/danh-gia/cho-duyet").hasAnyRole("QUAN_TRI", "HO_TRO") 
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/danh-gia/*/duyet").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/danh-gia/*").hasRole("QUAN_TRI")
        
                    // Cấu hình quyền cho API Yêu cầu hỗ trợ
                    .requestMatchers(HttpMethod.POST, "/api/v1/ho-tro").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro/nguoi-dung/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro/{id}").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro").hasAnyRole("QUAN_TRI", "HO_TRO") 
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro/trang-thai/**").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro/nguoi-phu-trach/**").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro/tim-kiem").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/ho-tro/thong-ke").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/ho-tro/*/trang-thai").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/ho-tro/*/uu-tien").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/ho-tro/*/phan-cong").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/ho-tro/**").hasRole("QUAN_TRI")
        
                    // Cấu hình quyền cho API Phản hồi yêu cầu
                    .requestMatchers(HttpMethod.POST, "/api/v1/phan-hoi").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/phan-hoi/yeu-cau/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/phan-hoi/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/phan-hoi/**").authenticated()

                    // Cấu hình quyền cho API Danh sách yêu thích
                    .requestMatchers(HttpMethod.POST, "/api/v1/yeu-thich").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/yeu-thich").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/yeu-thich/check").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/yeu-thich/thong-ke").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/yeu-thich/**").authenticated()

                    // Cấu hình quyền cho API Khuyến mãi
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai/mau-xe/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai/tinh-gia").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai/ma/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai/con-hieu-luc").hasAnyRole("QUAN_TRI", "BAN_HANG") 
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai/sap-het-han").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/khuyen-mai/{id}").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.POST, "/api/v1/khuyen-mai").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/khuyen-mai/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.POST, "/api/v1/khuyen-mai/kiem-tra-ap-dung").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.POST, "/api/v1/khuyen-mai/{id}/tang-su-dung").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/khuyen-mai/**").hasRole("QUAN_TRI")
        
                    // Cấu hình cho API khuyến mãi trong đơn hàng
                    .requestMatchers(HttpMethod.POST, "/api/v1/don-hang/kiem-tra-khuyen-mai").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/don-hang/khuyen-mai-phu-hop").authenticated()

                    // Cấu hình quyền cho API Đăng ký nhận tin
                    .requestMatchers(HttpMethod.POST, "/api/v1/dang-ky-nhan-tin/dang-ky").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/dang-ky-nhan-tin/huy-dang-ky").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/dang-ky-nhan-tin").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/dang-ky-nhan-tin/active").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/dang-ky-nhan-tin/{id}").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/dang-ky-nhan-tin/email").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/dang-ky-nhan-tin/{id}").hasAnyRole("QUAN_TRI", "HO_TRO")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/dang-ky-nhan-tin/{id}").hasRole("QUAN_TRI")

                    // Cấu hình quyền cho API Nhật ký hoạt động
                    .requestMatchers("/api/v1/nhat-ky/**").hasRole("QUAN_TRI")

                    // Cấu hình quyền cho API Hồ sơ trả góp
                    .requestMatchers(HttpMethod.GET, "/api/v1/tra-gop").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/tra-gop/thong-ke").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/tra-gop/trang-thai/**").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.GET, "/api/v1/tra-gop/{id}").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/tra-gop/don-hang/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/tra-gop/nguoi-dung/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/tra-gop").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/tra-gop/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/tra-gop/*/phe-duyet").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/tra-gop/*/tu-choi").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/tra-gop/*/hoan-thanh").hasAnyRole("QUAN_TRI", "BAN_HANG")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/tra-gop/**").hasAnyRole("QUAN_TRI", "BAN_HANG")

                    // Cấu hình quyền cho API Thống kê
                    .requestMatchers("/api/v1/thong-ke/**").hasAnyRole("QUAN_TRI", "BAN_HANG")

                    // Cấu hình quyền cho API Hình ảnh xe
                    .requestMatchers(HttpMethod.GET, "/api/v1/hinh-anh/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/hinh-anh/**").hasAnyRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/hinh-anh/**").hasAnyRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/hinh-anh/**").hasAnyRole("QUAN_TRI")

                    // Cấu hình quyền cho API Tìm kiếm nâng cao
                    .requestMatchers(HttpMethod.POST, "/api/v1/tim-kiem/mau-xe").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/tim-kiem/dai-ly").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/tim-kiem/ton-kho").permitAll()

                    // Thêm vào phần cấu hình quyền
                    .requestMatchers("/api/v1/quyen/**").hasRole("QUAN_TRI")
                    .requestMatchers("/api/v1/nhom-quyen/**").hasRole("QUAN_TRI")
                    .requestMatchers("/api/v1/quan-ly-nhan-vien/**").hasRole("QUAN_TRI")

                    .requestMatchers(HttpMethod.GET, "/api/v1/mau-sac/**").permitAll()

                    .requestMatchers(HttpMethod.GET, "/api/v1/hinh-anh-theo-mau/**").permitAll()

                    .requestMatchers(HttpMethod.GET, "/api/v1/noi-that/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/noi-that/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/noi-that/**").hasRole("QUAN_TRI")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/noi-that/**").hasRole("QUAN_TRI")

                    .anyRequest().authenticated()
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
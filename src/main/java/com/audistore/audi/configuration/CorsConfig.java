package com.audistore.audi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration shared by Spring MVC and Spring Security.
 *
 * The allowed origins are driven by APP_CORS_ORIGINS so the same image can run
 * on localhost, Firebase Hosting (audivn / audi-1109) and any custom domain
 * without a code change.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String DEFAULT_ORIGINS =
            "http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,"
            + "https://audivn.web.app,https://audivn.firebaseapp.com,"
            + "https://audi-1109.web.app,https://audi-1109.firebaseapp.com,"
            + "https://audi-vietnam-web.web.app,https://audi-vietnam-web.firebaseapp.com";

    @Value("${app.cors.allowed-origins:" + DEFAULT_ORIGINS + "}")
    private String allowedOrigins;

    private List<String> originPatterns() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private void apply(CorsConfiguration configuration) {
        configuration.setAllowedOriginPatterns(originPatterns());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition", "Content-Length", "Content-Type"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(originPatterns().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition", "Content-Length", "Content-Type")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        apply(configuration);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

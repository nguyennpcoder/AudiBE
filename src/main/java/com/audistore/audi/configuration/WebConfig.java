//package com.audistore.audi.configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*") // Thay đổi từ allowedOrigins sang allowedOriginPatterns
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                .allowedHeaders("*")
//                .exposedHeaders("Authorization")
//                .allowCredentials(false)
//                .maxAge(3600);
//    }
//}
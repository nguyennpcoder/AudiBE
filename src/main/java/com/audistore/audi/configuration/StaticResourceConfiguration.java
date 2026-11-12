package com.audistore.audi.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceConfiguration.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure for vehicle images
        String uploadDirVehicles = "uploads/images/vehicles";
        Path uploadPathVehicles = Paths.get(uploadDirVehicles);
        File uploadDirFileVehicles = uploadPathVehicles.toFile();

        // Create the uploads directory if it doesn't exist
        if (!uploadDirFileVehicles.exists()) {
            boolean created = uploadDirFileVehicles.mkdirs();
            logger.info("Uploads directory for vehicles {}: {}", uploadDirFileVehicles.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Configure for color images
        String uploadDirColors = "uploads/images/colors";
        Path uploadPathColors = Paths.get(uploadDirColors);
        File uploadDirFileColors = uploadPathColors.toFile();

        // Create the uploads directory for colors if it doesn't exist
        if (!uploadDirFileColors.exists()) {
            boolean created = uploadDirFileColors.mkdirs();
            logger.info("Uploads directory for colors {}: {}", uploadDirFileColors.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Configure for interior images
        String uploadDirInteriors = "uploads/images/interiors";
        Path uploadPathInteriors = Paths.get(uploadDirInteriors);
        File uploadDirFileInteriors = uploadPathInteriors.toFile();

        // Create the uploads directory for interiors if it doesn't exist
        if (!uploadDirFileInteriors.exists()) {
            boolean created = uploadDirFileInteriors.mkdirs();
            logger.info("Uploads directory for interiors {}: {}", uploadDirFileInteriors.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Configure for vehicles interiors images
        String uploadDirVehiclesInteriors = "uploads/images/vehicles_interiors";
        Path uploadPathVehiclesInteriors = Paths.get(uploadDirVehiclesInteriors);
        File uploadDirFileVehiclesInteriors = uploadPathVehiclesInteriors.toFile();

        // Create the uploads directory for vehicles interiors if it doesn't exist
        if (!uploadDirFileVehiclesInteriors.exists()) {
            boolean created = uploadDirFileVehiclesInteriors.mkdirs();
            logger.info("Uploads directory for vehicles interiors {}: {}", uploadDirFileVehiclesInteriors.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Configure for avatar user images
        String uploadDirAvatarUser = "uploads/images/avatar_user";
        Path uploadPathAvatarUser = Paths.get(uploadDirAvatarUser);
        File uploadDirFileAvatarUser = uploadPathAvatarUser.toFile();

        // Create the uploads directory for avatar_user if it doesn't exist
        if (!uploadDirFileAvatarUser.exists()) {
            boolean created = uploadDirFileAvatarUser.mkdirs();
            logger.info("Uploads directory for avatar_user {}: {}", uploadDirFileAvatarUser.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Configure for blog images
        String uploadDirBlogs = "uploads/images/blogs";
        Path uploadPathBlogs = Paths.get(uploadDirBlogs);
        File uploadDirFileBlogs = uploadPathBlogs.toFile();

        // Create the uploads directory for blogs if it doesn't exist
        if (!uploadDirFileBlogs.exists()) {
            boolean created = uploadDirFileBlogs.mkdirs();
            logger.info("Uploads directory for blogs {}: {}", uploadDirFileBlogs.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Thêm cấu hình cho thư mục wheels
        String uploadDirWheels = "uploads/images/wheels";
        Path uploadPathWheels = Paths.get(uploadDirWheels);
        File uploadDirFileWheels = uploadPathWheels.toFile();

        // Create the uploads directory for wheels if it doesn't exist
        if (!uploadDirFileWheels.exists()) {
            boolean created = uploadDirFileWheels.mkdirs();
            logger.info("Uploads directory for wheels {}: {}", uploadDirFileWheels.getAbsolutePath(), created ? "created" : "already exists");
        }

        // Thêm cấu hình cho thư mục vehicles_wheels
        String uploadDirVehiclesWheels = "uploads/images/vehicles_wheels";
        Path uploadPathVehiclesWheels = Paths.get(uploadDirVehiclesWheels);
        File uploadDirFileVehiclesWheels = uploadPathVehiclesWheels.toFile();

        // Create the uploads directory for vehicles_wheels if it doesn't exist
        if (!uploadDirFileVehiclesWheels.exists()) {
            boolean created = uploadDirFileVehiclesWheels.mkdirs();
            logger.info("Uploads directory for vehicles_wheels {}: {}", uploadDirFileVehiclesWheels.getAbsolutePath(), created ? "created" : "already exists");
        }

        logger.info("Serving vehicle static resources from: {}", uploadDirFileVehicles.getAbsolutePath());
        logger.info("Serving color static resources from: {}", uploadDirFileColors.getAbsolutePath());
        logger.info("Serving interior static resources from: {}", uploadDirFileInteriors.getAbsolutePath());
        logger.info("Serving vehicles interiors static resources from: {}", uploadDirFileVehiclesInteriors.getAbsolutePath());
        logger.info("Serving avatar_user static resources from: {}", uploadDirFileAvatarUser.getAbsolutePath());
        logger.info("Serving blog static resources from: {}", uploadDirFileBlogs.getAbsolutePath());
        logger.info("Serving wheels static resources from: {}", uploadDirFileWheels.getAbsolutePath());
        logger.info("Serving vehicles_wheels static resources from: {}", uploadDirFileVehiclesWheels.getAbsolutePath());

        // Register root uploads directory resource handler
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);

        // Register vehicle images resource handler
        registry.addResourceHandler("/uploads/images/vehicles/**")
                .addResourceLocations("file:" + uploadPathVehicles.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register color images resource handler
        registry.addResourceHandler("/uploads/images/colors/**")
                .addResourceLocations("file:" + uploadPathColors.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register interior images resource handler
        registry.addResourceHandler("/uploads/images/interiors/**")
                .addResourceLocations("file:" + uploadPathInteriors.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register vehicles interiors images resource handler
        registry.addResourceHandler("/uploads/images/vehicles_interiors/**")
                .addResourceLocations("file:" + uploadPathVehiclesInteriors.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register avatar user images resource handler
        registry.addResourceHandler("/uploads/images/avatar_user/**")
                .addResourceLocations("file:" + uploadPathAvatarUser.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register blog images resource handler
        registry.addResourceHandler("/uploads/images/blogs/**")
                .addResourceLocations("file:" + uploadPathBlogs.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register wheels images resource handler
        registry.addResourceHandler("/uploads/images/wheels/**")
                .addResourceLocations("file:" + uploadPathWheels.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);

        // Register vehicles_wheels images resource handler
        registry.addResourceHandler("/uploads/images/vehicles_wheels/**")
                .addResourceLocations("file:" + uploadPathVehiclesWheels.toFile().getAbsolutePath() + File.separator)
                .setCachePeriod(3600);
    }
}
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

        logger.info("Serving vehicle static resources from: {}", uploadDirFileVehicles.getAbsolutePath());
        logger.info("Serving color static resources from: {}", uploadDirFileColors.getAbsolutePath());

        // Register vehicle images resource handler
        registry.addResourceHandler("uploads/images/vehicles/**")
                .addResourceLocations("file:" + uploadPathVehicles.toFile().getAbsolutePath() + File.separator);
        
        // Register color images resource handler
        registry.addResourceHandler("uploads/images/colors/**")
                .addResourceLocations("file:" + uploadPathColors.toFile().getAbsolutePath() + File.separator);
    }
}

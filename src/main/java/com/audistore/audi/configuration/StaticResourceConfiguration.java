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

        logger.info("Serving vehicle static resources from: {}", uploadDirFileVehicles.getAbsolutePath());
        logger.info("Serving color static resources from: {}", uploadDirFileColors.getAbsolutePath());
        logger.info("Serving interior static resources from: {}", uploadDirFileInteriors.getAbsolutePath());

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
    }
}
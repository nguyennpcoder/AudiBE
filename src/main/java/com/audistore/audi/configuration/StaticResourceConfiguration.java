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
        String uploadDir = "uploads/images/vehicles";
        Path uploadPath = Paths.get(uploadDir);
        File uploadDirFile = uploadPath.toFile();

        // Create the uploads directory if it doesn't exist
        if (!uploadDirFile.exists()) {
            boolean created = uploadDirFile.mkdirs();
            logger.info("Uploads directory {}: {}", uploadDirFile.getAbsolutePath(), created ? "created" : "already exists");
        }

        String uploadAbsolutePath = uploadDirFile.getAbsolutePath();
        logger.info("Serving static resources from: {}", uploadAbsolutePath);

        // Change this line - map the URL pattern without duplicating the path
        registry.addResourceHandler("/uploads/images/vehicles/**")
                .addResourceLocations("file:" + uploadPath.toFile().getAbsolutePath() + File.separator);
    }
}

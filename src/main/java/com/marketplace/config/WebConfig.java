package com.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web configuration class for serving static resources.
 * <p>
 * This configuration class overrides the default Spring MVC resource handling
 * to serve uploaded files (images) from the configured upload directory.
 * It maps the public URL pattern {@code /api/uploads/**} to the actual file system
 * location specified by {@code app.upload.dir} in the application properties.
 * </p>
 * <p>
 * This enables the frontend to access uploaded images directly via HTTP requests,
 * without requiring a separate web server or additional configuration.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * The directory where uploaded files are stored, read from application properties.
     * <p>
     * This value is injected from the {@code app.upload.dir} property in the
     * application configuration file. The path is resolved relative to the
     * application's working directory.
     * </p>
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Configures resource handlers for serving static resources.
     * <p>
     * This method registers a resource handler that maps requests to the URL
     * pattern {@code /api/uploads/**} to the file system location specified by
     * {@code uploadDir}. The upload directory path is normalized to an absolute
     * path to ensure consistent resource resolution across different environments.
     * </p>
     * <p>
     * After this configuration, uploaded files can be accessed via URLs like:
     * {@code http://localhost:8080/api/uploads/example-image.jpg}
     * </p>
     *
     * @param registry the {@link ResourceHandlerRegistry} to add resource handlers to
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
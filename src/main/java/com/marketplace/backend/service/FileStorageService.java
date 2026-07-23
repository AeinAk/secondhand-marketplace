package com.marketplace.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for storing and serving uploaded files (images).
 * <p>
 * This service handles the storage of multipart files uploaded by users, typically
 * images associated with listings. Files are stored in a configurable directory
 * with unique filenames to prevent collisions. The service also provides a method
 * to generate public URLs for accessing stored files.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class FileStorageService {

    /**
     * The root directory where uploaded files are stored.
     */
    private final Path uploadRoot;

    /**
     * Constructs a FileStorageService with the configured upload directory.
     * <p>
     * The directory is created if it does not exist. The path is normalized
     * to an absolute path to ensure consistent access.
     * </p>
     *
     * @param uploadDir the upload directory path from application properties
     * @throws IOException if the directory cannot be created
     */
    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
    }

    /**
     * Stores a multipart file on the server.
     * <p>
     * Generates a unique filename using UUID to avoid name collisions.
     * The original file extension is preserved. The file is copied to the
     * configured upload directory. If the file is empty or null, an exception
     * is thrown.
     * </p>
     *
     * @param file the multipart file to store
     * @return the generated filename (without path)
     * @throws IllegalArgumentException if the file is null or empty
     * @throws RuntimeException         if an I/O error occurs during storage
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String original = file.getOriginalFilename();
        String extension = "";
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + extension;
        try {
            Path target = uploadRoot.resolve(filename);
            Files.copy(file.getInputStream(), target);
            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    /**
     * Generates a public URL for accessing a stored file.
     * <p>
     * The URL is prefixed with the API base path for serving static resources.
     * This URL can be used in the frontend to display images.
     * </p>
     *
     * @param filename the stored filename
     * @return the public URL string
     */
    public String toPublicUrl(String filename) {
        return "/api/uploads/" + filename;
    }
}
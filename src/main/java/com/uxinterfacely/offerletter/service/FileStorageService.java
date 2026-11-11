package com.uxinterfacely.offerletter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root;
    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("PDF file is required");
        String type = file.getContentType();
        if (type != null && !type.equalsIgnoreCase("application/pdf") && !type.equalsIgnoreCase("application/octet-stream"))
            throw new IllegalArgumentException("Only PDF files are accepted");
        if (file.getSize() > 25L * 1024 * 1024) throw new IllegalArgumentException("PDF must be 25 MB or smaller");
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "offer-letter.pdf" : file.getOriginalFilename());
        String safe = original.replaceAll("[^A-Za-z0-9._-]", "_");
        String name = UUID.randomUUID() + "-" + safe;
        Path target = root.resolve(name).normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid filename");
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/files/" + name;
    }
}

package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @PostConstruct
    public void init() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    /**
     * Сохраняет файл и возвращает имя сохраненного файла
     */
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadPath, newFilename);

        Files.copy(file.getInputStream(), filePath);

        return newFilename;
    }

    /**
     * Получает байты файла по имени
     */
    public byte[] getImageBytes(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IOException("Filename is empty");
        }

        Path filePath = Paths.get(uploadPath, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filename);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Удаляет файл
     */
    public void deleteImage(String filename) throws IOException {
        if (filename != null && !filename.isEmpty()) {
            Path filePath = Paths.get(uploadPath, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }
    }

    /**
     * Генерирует URL для доступа к изображению
     */
    public String getImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        if (filename.startsWith("http")) {
            return filename;
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(filename)
                .toUriString();
    }
}
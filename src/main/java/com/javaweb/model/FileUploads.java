package com.javaweb.model;

import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUploads {

    @Value("${upload.path:uploads}")
    private String uploadDir;

    public String fileUpload(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new ApplicationException(ErrorCode.FILE_IS_EMPTY);
        }
        String uploadPath = new File(uploadDir).getAbsolutePath();
        Path directoryPath = Paths.get(uploadPath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        String fileName = multipartFile.getOriginalFilename();
        Path path = Paths.get(uploadPath, fileName);
        Files.write(path, multipartFile.getBytes());
        return fileName;
    }
}

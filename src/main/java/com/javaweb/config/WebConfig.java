package com.javaweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.path:uploads}")
    private String uploadDir;


//    addResourceHandler("/images/**")	Xử lý yêu cầu có đường dẫn /images/**
// addResourceLocations("file:uploads/")	Tìm file trong thư mục uploads/
// Kết quả	Khi truy cập /images/heh.jpg, Spring trả về uploads/heh.jpg

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        //ánh xạ tất cả yêu cầu tới /images/** tới thư mục uploadDir
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/")
                .setCachePeriod(3600);
    }
}

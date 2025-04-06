package com.javaweb.controller;

import com.javaweb.dto.request.ProductRequest;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/upload")
    public ModelAndView upload() {
        return new ModelAndView("/test/uploads");
    }

    @PostMapping("/upload")
    public ModelAndView upload(@ModelAttribute ProductRequest productRequest,
                               @RequestPart(value = "file", required = false)MultipartFile multipartFile) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        // Debug dữ liệu nhận được
        System.out.println("Received data: " + productRequest);
        if (multipartFile != null) {
            System.out.println("Received file: " + multipartFile.getOriginalFilename());
        } else {
            System.out.println("No file received");
        }

        if (productService.save(productRequest, multipartFile)) {
            modelAndView.addObject("newProduct", "true");
            modelAndView.addObject("message", "Thêm sản phẩm thành công!");
        }

//        if (file.isEmpty()) {
//            modelAndView.addObject("message", "Vui lòng chọn file để upload");
//            return modelAndView;
//        }
//        try {
//            String uploadPath = new File(uploadDir).getAbsolutePath();
//            Path directoryPath = Paths.get(uploadPath);
//            if (!Files.exists(directoryPath)) {
//                Files.createDirectories(directoryPath);
//            }
//
//            // Lưu file
//            String fileName = file.getOriginalFilename();
//            Path filePath = Paths.get(uploadPath, fileName);
//            Files.write(filePath, file.getBytes());
//
//            // Truyền dữ liệu cho view
//            modelAndView.addObject("message", "Upload thành công: " + fileName);
//            modelAndView.addObject("fileName", fileName);
//            modelAndView.addObject("filePath", "/images/" + fileName);
//
//            // Kiểm tra xem file có phải là ảnh không
//            String contentType = file.getContentType();
//            boolean isImage = contentType != null && contentType.startsWith("image");
//            modelAndView.addObject("isImage", isImage);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            modelAndView.addObject("message", "Upload thất bại: " + e.getMessage());
//        }
        return modelAndView;
    }
}

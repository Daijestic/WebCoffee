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
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/upload")
    public ModelAndView upload() {
        return new ModelAndView("/test/uploads");
    }

    @PostMapping("/update/{id}")
    public ModelAndView update(@PathVariable Long id,
                               @ModelAttribute ProductRequest productRequest,
                               @RequestPart(value = "file", required = false)MultipartFile multipartFile)
                                throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        // Debug dữ liệu nhận được
        System.out.println("Received data: " + productRequest);
        if (multipartFile != null) {
            System.out.println("Received file: " + multipartFile.getOriginalFilename());
        } else {
            System.out.println("No file received");
        }
        if (id != null) {
            productService.update(id, productRequest, multipartFile);
        } else {
            productService.save(productRequest, multipartFile);
        }

        modelAndView.setViewName("redirect:/admin/products");
        return modelAndView;
    }
    @PostMapping("/upload")
    public ModelAndView upload(@ModelAttribute ProductRequest productRequest,
                               @RequestPart(value = "file", required = false)MultipartFile multipartFile)
            throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        productService.save(productRequest, multipartFile);
        modelAndView.setViewName("redirect:/admin/products");
        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView();
        productService.delete(id);
        modelAndView.setViewName("redirect:/admin/products");
        return modelAndView;
    }
}

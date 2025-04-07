package com.javaweb.controller;

import com.javaweb.converter.entitytodto.UserEntityToDTO;
import com.javaweb.custom.CustomUserDetails;
import com.javaweb.dto.reponse.APIResponse;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.service.KhachHangService;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserEntityToDTO userEntityToDTO;

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView ("redirect:/admin/");
    }

    @GetMapping("/")
    public ModelAndView showAdminIndex(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ModelAndView modelAndView = new ModelAndView("admin/index");
        modelAndView.addObject("admin", userDetails);
        return modelAndView;
    }

    @GetMapping("/products")
    public ModelAndView findAllProducts() {
        ModelAndView modelAndView = new ModelAndView("admin/productview");
        List<ProductResponse> products = productService.getAllProducts();
        Set<String> categories = products.stream()
                .map(ProductResponse::getLoaiMon)
                .collect(Collectors.toSet());
        modelAndView.addObject("products", products);
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/users")
    public APIResponse<List<UserResponse>> all() {
        return APIResponse.<List<UserResponse>>builder()
                .code(200)
                .message("success")
                .result(khachHangService.findAll())
                .build();
    }
}

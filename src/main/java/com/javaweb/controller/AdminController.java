package com.javaweb.controller;

import com.javaweb.custom.CustomUserDetails;
import com.javaweb.dto.reponse.APIResponse;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

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
        APIResponse<List<ProductResponse>> apiResponse = new APIResponse<List<ProductResponse>>();
        apiResponse.setCode(200);
        apiResponse.setMessage("OK");
        apiResponse.setResult(productService.getAllProducts());
        return new ModelAndView().addObject("response", apiResponse );
    }
}

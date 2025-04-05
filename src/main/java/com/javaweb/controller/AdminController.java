package com.javaweb.controller;

import com.javaweb.custom.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
}

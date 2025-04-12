package com.javaweb.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("webbuy/dangnhap");
        return mav;
    }
    @GetMapping("/oauth2/success")
    public String oauth2Success(Authentication authentication) {
        return "redirect:/muangay";
    }
}

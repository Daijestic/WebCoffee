package com.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("index"); // Trả về home.html
        return modelAndView;
    }
    @GetMapping("/web/quancaphe")
    public String freezetraxanh() {
        return "web/quancaphe";
    }
}
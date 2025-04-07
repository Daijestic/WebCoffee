package com.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("web/muangay"); // Trả về home.html
        return modelAndView;
    }
    @GetMapping("/khampha")
    public String khampha(){
        return "web/khampha";
    }
    @GetMapping("/web/quancaphe")
    public String quancaphe() {
        return "web/quancaphe";
    }
    @GetMapping("/web/thucdon")
    public String thucdon() {
        return "web/thucdon";
    }
    @GetMapping("/web/dongcaphedacbiet")
    public String dongcaphedacbiet() {
        return "web/dongcaphedacbiet";
    }
}
package com.javaweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping
    public ModelAndView index() {
        return new ModelAndView ("redirect:/admin/");
    }

    @RequestMapping("/")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("/index");
        return modelAndView;
    }
}

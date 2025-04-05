package com.javaweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @GetMapping("/logon")
    public ModelAndView logon() {
        ModelAndView mv = new ModelAndView("admin/dangnhap");
        return mv;
    }
}

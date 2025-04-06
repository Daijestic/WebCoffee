package com.javaweb.controller;

import com.javaweb.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class UserController {
    @Autowired
    KhachHangService khachHangService;

    @GetMapping("/user/my-info")
    public ModelAndView myInfo() {
        ModelAndView mav = new ModelAndView("/web/user");
        mav.addObject("user", khachHangService.getMyInfo());
        return new ModelAndView("/index");
    }

    @GetMapping("/dangky")
    public ModelAndView dangky() {
        ModelAndView mav = new ModelAndView("/web/dangky");
        return mav;
    }

}

package com.javaweb.controller;

import com.javaweb.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    @Autowired
    UsersService usersService;

    @GetMapping("/user/my-info")
    public ModelAndView myInfo() {
        ModelAndView mav = new ModelAndView("/web/user");
        mav.addObject("user", usersService.getMyInfo());
        return new ModelAndView("/index");
    }

    @GetMapping("/dangky")
    public ModelAndView dangky() {
        ModelAndView mav = new ModelAndView("/webbuy/dangky");
        return mav;
    }

//    @GetMapping("/thucdon")
//    public String thucdon() {
//       return  "/web/thucdon";
//    }
}

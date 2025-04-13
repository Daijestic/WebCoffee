package com.javaweb.controller;


import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/muangay")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("web/muangay"); // Trả về home.html
        return modelAndView;
    }
    @GetMapping("/index")
    public String index(){
        return "/index";
    }
    @GetMapping("/khampha")
    public String khampha(){
        return "web/khampha";
    }

    @GetMapping("/tinhhoatra")
    public String tinhhoatra(){
        return "web/tinhhoatra";
    }

    @GetMapping("/quancaphe")
    public String quancaphe() {
        return "web/quancaphe";
    }
    @GetMapping("/menunguyenban")
    public String menunguyenban() {
        return "web/menunguyenban";
    }
    @GetMapping("/thucdon")
    public String thucdon() {
        return "web/thucdon";
    }

    @Autowired
    private MonRepository monRepository;

    @GetMapping("/dongcaphedacbiet")
    public String dongcaphedacbiet(Model model) {
        List<MonEntity> danhSachMon = monRepository.findMonByLoaiMonId(3L); // ID = 3
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/dongcaphedacbiet"; // Trả về view
    }


    @GetMapping("/datdouong")
    public String showFlashSale(Model model) {
        Map<String, List<MonEntity>> categorizedMenu = new LinkedHashMap<>();
        categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId(3L));
        categorizedMenu.put("PHINDI", monRepository.findMonByLoaiMonId(4L));
        categorizedMenu.put("TRÀ", monRepository.findMonByLoaiMonId(5L));
        categorizedMenu.put("FREEZE", monRepository.findMonByLoaiMonId(6L));
        categorizedMenu.put("BÁNH MỲ QUE", monRepository.findMonByLoaiMonId(8L));
        categorizedMenu.put("BÁNH", monRepository.findMonByLoaiMonId(10L));

        model.addAttribute("menuMap", categorizedMenu);
        return "web/datdouong";
    }
}
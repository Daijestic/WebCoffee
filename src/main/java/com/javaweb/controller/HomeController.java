package com.javaweb.controller;


import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.repository.TaiKhoanRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import java.security.Principal;
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
        List<MonEntity> danhSachMon = monRepository.findAllByLoaiMon("Cà phê phin"); // ID = 3
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/dongcaphedacbiet"; // Trả về view
    }

    @Autowired
    private TaiKhoanRespository taiKhoanRespository;

    @GetMapping("/datdouong")
    public String showFlashSale(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // lấy tên đăng nhập
            System.out.println("Username: " + username);  // Thêm dòng debug để kiểm tra

            // Tạm thời chỉ trả về tên đăng nhập
            model.addAttribute("tenNguoiDung", username);
        } else {
            System.out.println("Không có thông tin người dùng đăng nhập");
        }

        Map<String, List<MonEntity>> categorizedMenu = new LinkedHashMap<>();
        categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId("CÀ PHÊ PHIN"));
        categorizedMenu.put("PHINDI", monRepository.findMonByLoaiMonId("PHINDI"));
        categorizedMenu.put("TRÀ", monRepository.findMonByLoaiMonId("TRÀ"));
        categorizedMenu.put("FREEZE", monRepository.findMonByLoaiMonId("FREEZE"));
        categorizedMenu.put("BÁNH MỲ QUE", monRepository.findMonByLoaiMonId("BÁNH MỲ QUE"));
        categorizedMenu.put("BÁNH", monRepository.findMonByLoaiMonId("BÁNH"));

        model.addAttribute("menuMap", categorizedMenu);
        return "web/datdouong";
    }
}
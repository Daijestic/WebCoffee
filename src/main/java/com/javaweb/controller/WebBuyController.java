package com.javaweb.controller;

import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebBuyController {

    @Autowired
    private MonRepository monRepository;

    @GetMapping("/xemtatca")
    public String hienThiSanPhamTheoLoai(@RequestParam("loai") String loai, Model model) {
        Map<String, List<MonEntity>> categorizedMenu = new LinkedHashMap<>();

        // Kiểm tra giá trị của tham số loai và phân loại món theo đó
        if (loai.equals("CÀ PHÊ PHIN")) {
            categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId(3L));
        } else if (loai.equals("PHINDI")) {
            categorizedMenu.put("PHINDI", monRepository.findMonByLoaiMonId(4L));
        } else if (loai.equals("TRÀ")) {
            categorizedMenu.put("TRÀ", monRepository.findMonByLoaiMonId(5L));
        } else if (loai.equals("FREEZE")) {
            categorizedMenu.put("FREEZE", monRepository.findMonByLoaiMonId(6L));
        } else if (loai.equals("BÁNH MỲ QUE")) {
            categorizedMenu.put("BÁNH MỲ QUE", monRepository.findMonByLoaiMonId(8L));
        } else if (loai.equals("BÁNH")) {
            categorizedMenu.put("BÁNH", monRepository.findMonByLoaiMonId(10L));
        }

        model.addAttribute("menuMap", categorizedMenu);

        // Trả về view (ví dụ: trang sản phẩm)
        return "webbuy/xemtatca"; // Thay "san-pham" bằng tên view của bạn
    }

    @GetMapping("/thanhtoan")
    public String thanhtoan() {
        return "webbuy/thanhtoan";
    }
}

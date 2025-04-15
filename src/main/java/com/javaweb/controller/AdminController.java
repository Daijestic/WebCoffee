package com.javaweb.controller;

import com.javaweb.converter.entitytodto.UserEntityToDTO;
import com.javaweb.custom.CustomUserDetails;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.KhachHangService;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserEntityToDTO userEntityToDTO;

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView ("redirect:/admin/");
    }

    @GetMapping("/")
    public ModelAndView showAdminIndex(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ModelAndView modelAndView = new ModelAndView("admin/index");
        modelAndView.addObject("admin", userDetails);
        return modelAndView;
    }

    @GetMapping("/products")
    public ModelAndView findAllProducts() {
        ModelAndView modelAndView = new ModelAndView("admin/productview");
        List<ProductResponse> products = productService.getAllProducts();
        Set<String> categories = products.stream()
                .map(ProductResponse::getLoaiMon)
                .collect(Collectors.toSet());
        modelAndView.addObject("products", products);
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/users")
    public ModelAndView all() {
        ModelAndView modelAndView = new ModelAndView("admin/users");
        modelAndView.addObject("users", khachHangService.findAll());
        return modelAndView;
    }

    @Autowired
    private MonRepository monRepository;

    @GetMapping("/datmon")
    public String hienThiSanPhamTheoLoai(
            @RequestParam(name = "loai", defaultValue = "") String loai,
            Model model) {
        Map<String, List<MonEntity>> categorizedMenu = new LinkedHashMap<>();
        System.out.println("Loại món được yêu cầu: " + loai);
        if (loai.isEmpty()) {
            // Nếu không truyền 'loai' thì lấy tất cả món
            categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId(3L));
        } else {
            switch (loai) {
                case "CÀ PHÊ PHIN":
                    categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId(3L));
                    break;
                case "PHINDI":
                    categorizedMenu.put("PHINDI", monRepository.findMonByLoaiMonId(4L));
                    break;
                case "TRÀ":
                    categorizedMenu.put("TRÀ", monRepository.findMonByLoaiMonId(5L));
                    break;
                case "FREEZE":
                    categorizedMenu.put("FREEZE", monRepository.findMonByLoaiMonId(6L));
                    break;
                case "BÁNH MỲ QUE":
                    categorizedMenu.put("BÁNH MỲ QUE", monRepository.findMonByLoaiMonId(8L));
                    break;
                default:
                    categorizedMenu.put("BÁNH", monRepository.findMonByLoaiMonId(10L));
            }
        }

        model.addAttribute("menuMap", categorizedMenu);
        return "admin/datmon";
    }

}

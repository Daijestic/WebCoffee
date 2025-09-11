package com.javaweb.controller;


import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.repository.TaiKhoanRespository;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

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
    public String tinhhoatra(Model model){
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("Trà"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/tinhhoatra";
    }

    @GetMapping("/quancaphe")
    public String quancaphe() {
        return "web/quancaphe";
    }
    @GetMapping("/menunguyenban")
    public String menunguyenban(Model model) {
        List<ProductResponse> danhSachMon = new ArrayList<>();
        danhSachMon.addAll(productService.findAllByLoaiMon("Freeze"));
        danhSachMon.addAll(productService.findAllByLoaiMon("Cà Phê Phin"));
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/menunguyenban";
    }
    @GetMapping("/thucdonmonan")
    public String thucdon(Model model) {
        List<ProductResponse> danhSachMon = new ArrayList<>();
        danhSachMon.addAll(productService.findAllByLoaiMon("Bánh Ngọt"));
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/thucdonmonan";
    }


    @Autowired
    private MonRepository monRepository;

    @GetMapping("/dongcaphedacbiet")
    public String dongcaphedacbiet(Model model) {
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("Cà phê phin"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/dongcaphedacbiet"; // Trả về view
    }
    @GetMapping("/caphe/{id}/{loaiMon}")
    public String chiTietMon(@PathVariable String id,
                             @PathVariable String loaiMon,
                             Model model) {
        ProductResponse mon = (ProductResponse) productService.findByTenMon(id);
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon(loaiMon);

        model.addAttribute("mon", mon);
        model.addAttribute("danhSachMon", danhSachMon);
        return "web/trathachdao";
    }
    @GetMapping("/thucdon")
    public String thucdon(){
        return "web/thucdon";
    }
    @GetMapping("/caphe")
    public String caphe(Model model) {
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("Cà phê phin"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/caphe"; // Trả về view
    }
    @GetMapping("/caphephin")
    public String caphephin(Model model) {
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("Cà phê phin"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/caphephin"; // Trả về view
    }
    @GetMapping("/phindi")
    public String phindi(Model model) {
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("PhinDi"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/caphe"; // Trả về view
    }
    @GetMapping("/freeze")
    public String freeze(Model model) {
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("Freeze"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/freeze"; // Trả về view
    }
    @GetMapping("/tra")
    public String tra(Model model) {
        List<ProductResponse> danhSachMon = productService.findAllByLoaiMon("Trà"); // ID = 3
        //ProductResponse productResponse = productService.findById(1L);
        model.addAttribute("danhSachMon", danhSachMon); // Sử dụng addAttribute
        return "web/tra"; // Trả về view
    }
    @GetMapping("/thucdongiaohang")
    public String thucdongiaohang(){
        return "web/thucdongiaohang";
    }
    @GetMapping("/tintuc")
    public String tintuc(){
        return "web/tintuc";
    }
    @GetMapping("/nguongoc")
    public String nguongoc(){
        return "web/nguongoc";
    }
    @GetMapping("/dichvu")
    public String dichvu(){
        return "web/dichvu";
    }
    @GetMapping("/nghenghiep")
    public String nghenghiep(){
        return "web/nghenghiep";
    }
    @GetMapping("/vechungtoi")
    public String vechungtoi(){
        return "web/vechungtoi";
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

        Map<String, List<ProductResponse>> categorizedMenu = new LinkedHashMap<>();
        categorizedMenu.put("Cà phê phin", productService.findAllByLoaiMon("Cà phê phin"));
        categorizedMenu.put("PhinDi", productService.findAllByLoaiMon("PhinDi"));
        categorizedMenu.put("Trà", productService.findAllByLoaiMon("Trà"));
        categorizedMenu.put("Freeze",productService.findAllByLoaiMon("Freeze"));
        categorizedMenu.put("Bánh Ngọt",productService.findAllByLoaiMon("Bánh Ngọt"));
        categorizedMenu.put("Sản phẩm cà phê", productService.findAllByLoaiMon("Sản phẩm cà phê"));
        model.addAttribute("menuMap", categorizedMenu);
        return "web/datdouong";
    }
}
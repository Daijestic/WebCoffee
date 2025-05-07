package com.javaweb.controller;

import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.custom.CustomUserDetails;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.KhachHangService;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
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

    @Autowired
    private MonRepository monRepository;

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

//    @PostMapping("/users/add")
//    @ResponseBody
//    public ModelAndView addUser(@RequestBody UserRequest userRequest) {
//        ModelAndView modelAndView = new ModelAndView();
//        khachHangService.save(userRequest);
//        modelAndView.setViewName("redirect:/admin/users");
//        return modelAndView;
////        return khachHangService.save(userRequest);
//    }

    @PostMapping("/users/add")
    public ResponseEntity<?> addOrUpdateUser(@RequestBody UserRequest userRequest) {
        try {
            UserResponse userResponse = new UserResponse();
            Map<String, Object> response = new HashMap<>();
            if (userRequest.getId() == null) {
                userResponse = khachHangService.save(userRequest);
                response.put("message", "Thêm người dùng thành công");
            } else {
                userResponse = khachHangService.update(userRequest);
                response.put("message", "Cập nhật người dùng thành công");
            }

            // Trả về dữ liệu JSON với thông tin chi tiết hơn
            response.put("success", true);
            response.put("userId", userResponse.getId());  // Nếu có

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Trả về lỗi
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @GetMapping("/users/{userId}")
    @ResponseBody
    public UserResponse userDetail(@PathVariable Long userId) {
        return khachHangService.findById(userId);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> userDelete(@PathVariable Long userId) {
        try {
            khachHangService.deleteKhachHangById(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa người dùng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


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

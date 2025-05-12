package com.javaweb.controller;

import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.AddToCartRequest;
import com.javaweb.entity.ChiTietGioHangEntity;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.service.GioHangService;
import com.javaweb.service.ProductService;
import com.javaweb.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebBuyController {
    @Autowired
    private ProductService productService;


    @GetMapping("/Xemtatca/{loaiMon}")
    public String chiTietLoai(@PathVariable String loaiMon,
                             Model model,Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // Lấy tên đăng nhập
            System.out.println("Username: " + username);

            model.addAttribute("tenNguoiDung", username);

            // Tìm tài khoản theo username
            UserResponse khachHang = usersService.findByUsername(username);

            if (khachHang != null) {
                model.addAttribute("user", khachHang); // Gửi thông tin khách hàng vào model
            } else {
                System.out.println("Không tìm thấy khách hàng liên kết với tài khoản này.");
            }

        } else {
            System.out.println("Không có người dùng đăng nhập.");
        }
        Map<String, List<ProductResponse>> categorizedMenu = new LinkedHashMap<>();
        categorizedMenu.put(loaiMon, productService.findAllByLoaiMon(loaiMon));
        model.addAttribute("menuMap", categorizedMenu);
        return "webbuy/xemtatca";
    }

    @GetMapping("/thanhtoan")
    public String thanhtoan() {
        return "webbuy/thanhtoan";
    }

    @Autowired
    private UsersService usersService;

    @GetMapping("/ho-so")
    public String showHoSo(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // Lấy tên đăng nhập
            System.out.println("Username: " + username);

            model.addAttribute("tenNguoiDung", username);

            // Tìm tài khoản theo username
            UserResponse khachHang = usersService.findByUsername(username);

            if (khachHang != null) {
                model.addAttribute("user", khachHang); // Gửi thông tin khách hàng vào model
            } else {
                System.out.println("Không tìm thấy khách hàng liên kết với tài khoản này.");
            }

        } else {
            System.out.println("Không có người dùng đăng nhập.");
        }

        return "webbuy/trangcanhan";
    }
    @Autowired
    private GioHangService gioHangService;
    @GetMapping("/Xemchitiet/{loaiMon}")
    public String chiTietSanPham(@PathVariable String loaiMon,
                              Model model ,  Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // Lấy tên đăng nhập
            System.out.println("Username: " + username);

            model.addAttribute("tenNguoiDung", username);

            // Tìm tài khoản theo username
            UserResponse khachHang = usersService.findByUsername(username);

            if (khachHang != null) {
                model.addAttribute("user", khachHang); // Gửi thông tin khách hàng vào model
            } else {
                System.out.println("Không tìm thấy khách hàng liên kết với tài khoản này.");
            }

        } else {
            System.out.println("Không có người dùng đăng nhập.");
        }
        String username = principal.getName();
        Long userId = usersService.getUserIdByUsername(username);

        // Lấy danh sách chi tiết giỏ hàng
        List<ChiTietGioHangEntity> cartItems = gioHangService.getGioHangByUserId(userId);

        // Tính tổng tiền
        long tongTien = 0;
        for (ChiTietGioHangEntity item : cartItems) {
            // Lấy giá tiền từ món và size
            long giaTien = item.getMon().getGiaMonSizeEntities().stream()
                    .filter(gms -> gms.getId().getSizeId().equals(item.getSize().getIdSize()))
                    .findFirst()
                    .map(GiaMonSizeEntity::getGiaBan)
                    .orElse(0L);

            tongTien += giaTien * item.getSoLuong();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("tenNguoiDung", principal.getName());
        ProductResponse mon =  productService.findByTenMon(loaiMon);
        model.addAttribute("mon", mon);
        return "webbuy/chitietsanpham";
    }


}

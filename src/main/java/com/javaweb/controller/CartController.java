package com.javaweb.controller;

import com.javaweb.dto.reponse.CartResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.AddToCartRequest;
import com.javaweb.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gio-hang")
public class CartController {

    @Autowired
    private UsersService usersService;

    // API thêm vào giỏ hàng
    @PostMapping("/them")
    public ResponseEntity<Map<String, Object>> themVaoGio(@RequestBody AddToCartRequest request) {
        try {
            UserResponse userResponse = usersService.themVaoGioHang(request);
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Thêm vào giỏ hàng thành công!",
                            "user", userResponse
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    // API lấy số lượng sản phẩm trong giỏ hàng
    @GetMapping("/so-luong")
    public ResponseEntity<?> laySoLuongGioHang(@RequestParam Long userId) {
        try {
            Long cartCount = usersService.countByUserId(userId);
            return ResponseEntity.ok(Map.of("cartCount", cartCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể lấy số lượng giỏ hàng: " + e.getMessage()));
        }
    }

    // API lấy tất cả sản phẩm trong giỏ hàng
    @GetMapping("/danh-sach")
    public ResponseEntity<?> layDanhSachGioHang(@RequestParam Long userId) {
        try {
            List<CartResponse> cartItems = usersService.layDanhSachGioHang(userId);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể lấy danh sách giỏ hàng: " + e.getMessage()));
        }
    }

    // API cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/cap-nhat")
    public ResponseEntity<?> capNhatSoLuong(@RequestBody AddToCartRequest addToCartRequest) {
        try {
            usersService.capNhatSoLuong(addToCartRequest);
            return ResponseEntity.ok(Map.of("message", "Cập nhật số lượng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể cập nhật số lượng: " + e.getMessage()));
        }
    }

    // API xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/xoa")
    public ResponseEntity<?> xoaSanPham(@RequestBody AddToCartRequest addToCartRequest) {
        try {
            usersService.xoaSanPhamKhoiGioHang(addToCartRequest);
            return ResponseEntity.ok(Map.of("message", "Xóa sản phẩm thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể xóa sản phẩm: " + e.getMessage()));
        }
    }

    // API xóa toàn bộ giỏ hàng
    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<?> xoaTatCa(@RequestParam Long userId) {
        try {
            usersService.xoaGioHang(userId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ giỏ hàng!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể xóa giỏ hàng: " + e.getMessage()));
        }
    }
}
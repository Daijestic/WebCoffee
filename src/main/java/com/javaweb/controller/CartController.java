package com.javaweb.controller;

import com.javaweb.dto.reponse.CartResponse;
import com.javaweb.dto.request.CartRemoveRequest;
import com.javaweb.dto.request.CartUpdateRequest;
import com.javaweb.entity.GiaMonSizeEntity;
import org.springframework.ui.Model;
import com.javaweb.dto.request.AddToCartRequest;
import com.javaweb.entity.ChiTietGioHangEntity;
import com.javaweb.service.GioHangService;
import com.javaweb.service.UsersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestBody AddToCartRequest request, Principal principal) {
        System.out.println("Request: " + request);
        if (principal == null) {
            return ResponseEntity.badRequest().body("Vui lòng đăng nhập để thêm vào giỏ hàng");
        }
        System.out.println("Gọi vào controller thành công");
        String username = principal.getName();
        boolean success = usersService.addToCart(request, username);

        if (success) {
            return ResponseEntity.ok("Đã thêm sản phẩm vào giỏ hàng thành công");
        } else {
            return ResponseEntity.badRequest().body("Không thể thêm sản phẩm vào giỏ hàng");
        }
    }
    @Autowired
    private GioHangService gioHangService;

    @GetMapping("/api/cart")
    @ResponseBody
    public List<CartResponse> getCartItems(Principal principal) {
        if (principal == null) {
            return new ArrayList<>();
        }

        String username = principal.getName();
        Long userId = usersService.getUserIdByUsername(username);

        List<ChiTietGioHangEntity> cartItems = gioHangService.getGioHangByUserId(userId);
        List<CartResponse> response = new ArrayList<>();

        for (ChiTietGioHangEntity item : cartItems) {
            CartResponse cartItem = new CartResponse();
            cartItem.setIdMon(item.getMon().getIdMon());
            cartItem.setTenMon(item.getMon().getTenMon());
            cartItem.setTenSize(item.getSize().getTenSize());
            cartItem.setHinhAnh(item.getMon().getPath());

            Long giaBan = item.getMon().getGiaMonSizeEntities().stream()
                    .filter(gms -> gms.getId().getSizeId().equals(item.getSize().getIdSize()))
                    .findFirst()
                    .map(GiaMonSizeEntity::getGiaBan)
                    .orElse(0L);

            cartItem.setGiaBan(giaBan);
            cartItem.setSoLuong(item.getSoLuong());
            cartItem.setGhiChu(item.getGhiChu());

            response.add(cartItem);
        }

        return response;
    }
    @PostMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<String> updateCartQuantity(@RequestBody CartUpdateRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Vui lòng đăng nhập để cập nhật giỏ hàng");
        }

        String username = principal.getName();
        boolean success = gioHangService.updateCartItemQuantity(
                username, request.getIdMon(), request.getTenSize(), request.getSoLuongThayDoi());

        if (success) {
            return ResponseEntity.ok("Đã cập nhật giỏ hàng thành công");
        } else {
            return ResponseEntity.badRequest().body("Không thể cập nhật giỏ hàng");
        }
    }

    @PostMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<String> removeFromCart(@RequestBody CartRemoveRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Vui lòng đăng nhập để xóa sản phẩm");
        }

        String username = principal.getName();
        boolean success = gioHangService.removeCartItem(username, request.getIdMon(), request.getTenSize());

        if (success) {
            return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
        } else {
            return ResponseEntity.badRequest().body("Không thể xóa sản phẩm");
        }
    }
}

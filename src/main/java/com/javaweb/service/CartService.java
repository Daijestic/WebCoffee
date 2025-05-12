package com.javaweb.service;

import com.javaweb.dto.request.AddToCartRequest;
import com.javaweb.entity.*;
import com.javaweb.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CartService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final UserRepository userRepository;
    private final MonRepository monRepository;
    private final SizeRepository sizeRepository;

    public CartService(GioHangRepository gioHangRepository,
                       ChiTietGioHangRepository chiTietGioHangRepository,
                       UserRepository userRepository,
                       MonRepository monRepository,
                       SizeRepository sizeRepository) {
        this.gioHangRepository = gioHangRepository;
        this.chiTietGioHangRepository = chiTietGioHangRepository;
        this.userRepository = userRepository;
        this.monRepository = monRepository;
        this.sizeRepository = sizeRepository;
    }

    @Transactional
    public void themVaoGioHang(AddToCartRequest request) {
        Long userId = request.getUserId();
        Long monId = request.getMonId();
        Long sizeId = request.getSizeId();
        Long soLuong = request.getSoLuong();
        String ghiChu = request.getGhiChu();

        // Tìm UserEntity từ database
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        // 1. Kiểm tra giỏ hàng của người dùng
        GioHangEntity gioHang = gioHangRepository.findByUserId(userId)
                .orElseGet(() -> {
                    GioHangEntity newGioHang = new GioHangEntity();
                    newGioHang.setUser(user); // Liên kết đối tượng User
                    newGioHang.setNgayTao(new Date());
                    return gioHangRepository.save(newGioHang);
                });

        // 2. Kiểm tra sản phẩm có tồn tại trong ChiTietGioHang chưa
        ChiTietGioHangEntity.ChiTietGioHangId chiTietId =
                new ChiTietGioHangEntity.ChiTietGioHangId(monId, sizeId, gioHang.getID_GioHang());

        ChiTietGioHangEntity chiTiet = chiTietGioHangRepository.findById(chiTietId)
                .orElseGet(() -> {
                    ChiTietGioHangEntity newChiTiet = new ChiTietGioHangEntity();
                    newChiTiet.setId(chiTietId);
                    newChiTiet.setGioHang(gioHang);

                    // Lấy MonEntity từ database
                    MonEntity mon = monRepository.findById(monId)
                            .orElseThrow(() -> new RuntimeException("Món không tồn tại với ID: " + monId));
                    newChiTiet.setMon(mon);

                    // Lấy SizeEntity từ database
                    SizeEntity size = sizeRepository.findById(sizeId)
                            .orElseThrow(() -> new RuntimeException("Size không tồn tại với ID: " + sizeId));
                    newChiTiet.setSize(size);

                    newChiTiet.setSoLuong(0L); // Bắt đầu từ 0
                    return newChiTiet;
                });

        // Cập nhật số lượng và ghi chú
        chiTiet.setSoLuong(chiTiet.getSoLuong() + soLuong);
        chiTiet.setGhiChu(ghiChu);

        // Lưu lại vào database
        chiTietGioHangRepository.save(chiTiet);
    }
}
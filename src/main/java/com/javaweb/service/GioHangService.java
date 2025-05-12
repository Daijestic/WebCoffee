package com.javaweb.service;

import com.javaweb.entity.ChiTietGioHangEntity;
import com.javaweb.entity.ChiTietGioHangEntity.ChiTietGioHangId;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.ChiTietGioHangRepository;
import com.javaweb.repository.MonRepository;
import com.javaweb.repository.SizeRepository;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GioHangService {

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    private MonRepository monRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy tất cả các mục trong giỏ hàng của người dùng
     */
    public List<ChiTietGioHangEntity> getGioHangByUserId(Long userId) {
        return chiTietGioHangRepository.findByIdUserId(userId);
    }

    /**
     * Thêm một món vào giỏ hàng
     */
    public ChiTietGioHangEntity themVaoGioHang(Long monId, Long sizeId, Long userId, Long soLuong, String ghiChu) {
        // Tạo khóa chính
        ChiTietGioHangId id = new ChiTietGioHangId(monId, sizeId, userId);

        // Kiểm tra xem món này đã có trong giỏ hàng chưa
        Optional<ChiTietGioHangEntity> existingItem = chiTietGioHangRepository.findByIdMonIdAndIdSizeIdAndIdUserId(monId, sizeId, userId);

        if (existingItem.isPresent()) {
            // Nếu đã có, cập nhật số lượng
            ChiTietGioHangEntity item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            item.setGhiChu(ghiChu);
            return chiTietGioHangRepository.save(item);
        } else {
            // Nếu chưa có, tạo mới
            MonEntity mon = monRepository.findById(monId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món với ID: " + monId));

            SizeEntity size = sizeRepository.findById(sizeId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy size với ID: " + sizeId));

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            ChiTietGioHangEntity newItem = new ChiTietGioHangEntity(id, soLuong, ghiChu);
            newItem.setMon(mon);
            newItem.setSize(size);
            newItem.setUser(user);

            return chiTietGioHangRepository.save(newItem);
        }
    }

    /**
     * Cập nhật số lượng một món trong giỏ hàng
     */
    public ChiTietGioHangEntity capNhatSoLuong(Long monId, Long sizeId, Long userId, Long soLuongMoi) {
        ChiTietGioHangEntity item = chiTietGioHangRepository.findByIdMonIdAndIdSizeIdAndIdUserId(monId, sizeId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món trong giỏ hàng"));

        item.setSoLuong(soLuongMoi);
        return chiTietGioHangRepository.save(item);
    }

    /**
     * Xóa một món khỏi giỏ hàng
     */
    public void xoaKhoiGioHang(Long monId, Long sizeId, Long userId) {
        ChiTietGioHangId id = new ChiTietGioHangId(monId, sizeId, userId);
        chiTietGioHangRepository.deleteById(id);
    }

    /**
     * Xóa tất cả các món trong giỏ hàng của người dùng
     */
    public void xoaGioHang(Long userId) {
        List<ChiTietGioHangEntity> gioHang = chiTietGioHangRepository.findByIdUserId(userId);
        chiTietGioHangRepository.deleteAll(gioHang);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * @param username Tên đăng nhập của người dùng
     * @param idMon ID của món cần cập nhật
     * @param tenSize Tên size của món
     * @param soLuongThayDoi Số lượng thay đổi (có thể là số dương hoặc số âm)
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateCartItemQuantity(String username, Long idMon, String tenSize, int soLuongThayDoi) {
        try {
            // Tìm user theo tên đăng nhập
            UserEntity user = userRepository.findByDangNhap(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Tìm size theo tên
            SizeEntity size = sizeRepository.findByTenSize(tenSize)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước"));

            // Tìm chi tiết giỏ hàng
            ChiTietGioHangEntity chiTietGioHang = chiTietGioHangRepository
                    .findByIdMonIdAndIdSizeIdAndIdUserId(idMon, size.getIdSize(), user.getIdUser())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

            // Lấy số lượng hiện tại
            Long soLuongHienTai = chiTietGioHang.getSoLuong();

            // Tính số lượng mới
            Long soLuongMoi = soLuongHienTai + soLuongThayDoi;

            // Nếu số lượng mới <= 0, xóa sản phẩm khỏi giỏ hàng
            if (soLuongMoi <= 0) {
                xoaKhoiGioHang(idMon, size.getIdSize(), user.getIdUser());
            } else {
                // Cập nhật số lượng
                capNhatSoLuong(idMon, size.getIdSize(), user.getIdUser(), soLuongMoi);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * @param username Tên đăng nhập của người dùng
     * @param idMon ID của món cần xóa
     * @param tenSize Tên size của món
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean removeCartItem(String username, Long idMon, String tenSize) {
        try {
            // Tìm user theo tên đăng nhập
            UserEntity user = userRepository.findByDangNhap(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Tìm size theo tên
            SizeEntity size = sizeRepository.findByTenSize(tenSize)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước"));

            // Xóa sản phẩm khỏi giỏ hàng
            xoaKhoiGioHang(idMon, size.getIdSize(), user.getIdUser());

            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
            return false;
        }
    }
}
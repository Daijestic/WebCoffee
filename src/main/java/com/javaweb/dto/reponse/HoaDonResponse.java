package com.javaweb.dto.reponse;

import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class HoaDonResponse {
    private Date ngayGioLapHoaDon;
    private Long giamGia;
    private String phuongThucThanhToan;
    private String trangThai;
    private List<ChiTietHoaDonResponse> products;
}

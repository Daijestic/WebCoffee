package com.javaweb.dto.reponse;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonResponse {
    private Date ngayGioLapHoaDon;
    private Long giamGia;
    private String phuongThucThanhToan;
    private String trangThai;
    private List<ChiTietHoaDonResponse> products;
}

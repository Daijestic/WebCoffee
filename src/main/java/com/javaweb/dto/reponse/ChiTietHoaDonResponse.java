package com.javaweb.dto.reponse;

import lombok.*;

import java.util.List;

@Setter
@Getter
public class ChiTietHoaDonResponse {
    private Long idMon;
    private String tenMon;
    private String hinhAnh;
    private String size;
    private Long giaBan;
    private Long soLuong;
    private String ghiChu;
}

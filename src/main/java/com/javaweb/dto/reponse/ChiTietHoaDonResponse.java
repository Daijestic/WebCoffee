package com.javaweb.dto.reponse;

import lombok.*;

import java.util.List;

@Setter
@Getter
public class ChiTietHoaDonResponse {
    private ProductResponse product;
    private SizeResponse size;
    private Long soLuong;
    private String ghiChu;
}

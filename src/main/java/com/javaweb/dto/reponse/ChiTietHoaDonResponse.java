package com.javaweb.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietHoaDonResponse {
    private ProductResponse product;
    private Long soLuong;
    private Long ghiChu;
}

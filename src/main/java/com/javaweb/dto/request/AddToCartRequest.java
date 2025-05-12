package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private Long monId; // ID món
    private Long sizeId; // ID size
    private Long userId; // ID người dùng
    private Long soLuong; // Số lượng
    private String ghiChu; // Ghi chú
}


package com.javaweb.dto.reponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartResponse {
    private Long idMon;
    private String tenMon;
    private String tenSize;
    private String hinhAnh;
    private Long giaBan;
    private Long soLuong;
    private String ghiChu;
}

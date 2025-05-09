package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChiTietNhapKhoRequest {
    private Long idNguyenLieu;
    private String donViTinh;
    private Long soLuong;
    private Long giaTien;
}

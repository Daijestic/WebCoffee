package com.javaweb.dto.reponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChiTietNhapKhoResponse {
    private Long idNguyenLieu;
    private String tenNguyenLieu;
    private String donViTinh;
    private Long soLuong;
    private Long giaTien;
}

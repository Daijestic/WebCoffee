package com.javaweb.dto.reponse;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuNhapXuatNguyenLieuResponse {
    private Long idMaPhieu;
    private Date ngayNhapXuat;
    private Long soLuong;
    private String donViTinh;
    private String hinhThuc;
    private Long giaTien;
    private Long idUser;
    private String tenUser;
}

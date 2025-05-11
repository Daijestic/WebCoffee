package com.javaweb.dto.reponse;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PhieuXuatKhoResponse {
    private Long idPhieuXuatKho;
    private Date ngayXuat;
    private Long idNhanVien;
    private String tenNhanVien;
    List<ChiTietXuatKhoResponse> chiTietXuatKhoList;
}

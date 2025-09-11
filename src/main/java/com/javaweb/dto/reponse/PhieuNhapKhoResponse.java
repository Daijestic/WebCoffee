package com.javaweb.dto.reponse;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PhieuNhapKhoResponse {
    private Long idPhieuNhapKho;
    private Date ngayNhap;
    private Long idNhanVien;
    private String tenNhanVien;
    private Long idNhaCungCap;
    private String tenNhaCungCap;
    private List<ChiTietNhapKhoResponse> chiTietNhapKhoList;
}

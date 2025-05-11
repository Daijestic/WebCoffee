package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class PhieuNhapKhoRequest {
    private Long idPhieuNhapKho;
    private Date ngayNhap;
    private Long idNhanVien;
    private Long idNhaCungCap;
    private String ghiChu;
    private List<ChiTietNhapKhoRequest> chiTietNhapKhoList;
}

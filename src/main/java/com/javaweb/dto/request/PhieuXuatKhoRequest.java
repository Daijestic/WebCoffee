package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PhieuXuatKhoRequest {
    private Long idPhieuXuatKho;
    private Date ngayXuat;
    private Long idNhanVien;
    private List<ChiTietXuatKhoRequest> chiTietXuatKhoList;
}

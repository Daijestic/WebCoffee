package com.javaweb.dto.reponse;

import com.javaweb.entity.*;
import lombok.*;

import java.util.List;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffReponse {

    private Long id;

    private String hoTen;

    private String sdt;

    private String diaChi;

    private String email;

    private String username;

    private String role;

    private List<HoaDonEntity> hoaDon;

    private List<CaLamViecEntity> listCaLamViec;

    private List<PhieuNhapKhoEntity> listPhieuNhapKho;

    private List<PhieuXuatKhoEntity> listPhieuXuatKho;
}

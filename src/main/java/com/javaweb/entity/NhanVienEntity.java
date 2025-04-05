package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "NhanVien")
public class NhanVienEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "Email")
    private String email;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "TaiKhoan")
    private TaiKhoanEntity taiKhoan;

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDonEntity> hoaDon;

    @OneToMany(mappedBy = "nhanVien")
    private List<CaLamViecEntity> listCaLamViec;

    @OneToMany(mappedBy = "nhanVien")
    private List<PhieuNhapKhoEntity> listPhieuNhapKho;

    @OneToMany(mappedBy = "nhanVien")
    private List<PhieuXuatKhoEntity> listPhieuXuatKho;

}

package com.javaweb.entity;

import javax.persistence.*;

@Entity
@Table(name = "NguyenLieu")
public class NguyenLieuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenNguyenLieu")
    private String tenNguyenLieu;

    @Column(name = "DonViTinh")
    private String donViTinh;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Id_PhieuNhapKho")
    private PhieuNhapKhoEntity phieuNhap;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Id_PhieuXuatKho")
    private PhieuXuatKhoEntity phieuXuat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenNguyenLieu() {
        return tenNguyenLieu;
    }

    public void setTenNguyenLieu(String tenNguyenLieu) {
        this.tenNguyenLieu = tenNguyenLieu;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public PhieuNhapKhoEntity getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhapKhoEntity phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    public PhieuXuatKhoEntity getPhieuXuat() {
        return phieuXuat;
    }

    public void setPhieuXuat(PhieuXuatKhoEntity phieuXuat) {
        this.phieuXuat = phieuXuat;
    }
}

package com.javaweb.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Kho")
public class KhoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenKho")
    private String tenKho;

    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL)
    private List<PhieuNhapKhoEntity> listPhieuNhap;

    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL)
    private List<PhieuXuatKhoEntity> listPhieuXuat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenKho() {
        return tenKho;
    }

    public void setTenKho(String tenKho) {
        this.tenKho = tenKho;
    }

    public List<PhieuNhapKhoEntity> getListPhieuNhap() {
        return listPhieuNhap;
    }

    public void setListPhieuNhap(List<PhieuNhapKhoEntity> listPhieuNhap) {
        this.listPhieuNhap = listPhieuNhap;
    }

    public List<PhieuXuatKhoEntity> getListPhieuXuat() {
        return listPhieuXuat;
    }

    public void setListPhieuXuat(List<PhieuXuatKhoEntity> listPhieuXuat) {
        this.listPhieuXuat = listPhieuXuat;
    }
}

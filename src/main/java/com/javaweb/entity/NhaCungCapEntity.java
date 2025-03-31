package com.javaweb.entity;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "NhaCungCap")
public class NhaCungCapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenNCC")
    private String tenNCC;

    @Column(name = "SDT")
    private String SDT;

    @Column(name = "DiaChi")
    private String diaChi;

    @OneToMany(mappedBy = "nhaCungCap", cascade = CascadeType.ALL)
    private List<PhieuNhapKhoEntity> listPhieuNhapKho;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public List<PhieuNhapKhoEntity> getListPhieuNhapKho() {
        return listPhieuNhapKho;
    }

    public void setListPhieuNhapKho(List<PhieuNhapKhoEntity> listPhieuNhapKho) {
        this.listPhieuNhapKho = listPhieuNhapKho;
    }
}

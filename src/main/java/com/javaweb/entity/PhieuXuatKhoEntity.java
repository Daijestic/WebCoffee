package com.javaweb.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PhieuXuatKho")
public class PhieuXuatKhoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NgayXuat")
    private Date ngayXuat;

    @ManyToOne
    @JoinColumn(name = "Id_NhanVien")
    private NhanVienEntity nhanVien;

    @ManyToOne
    @JoinColumn(name = "Id_Kho")
    private KhoEntity kho;

    @OneToMany(mappedBy = "phieuXuat")
    private List<NguyenLieuEntity> listNguyenLieu;

    @Column(name = "SoLuong")
    private Long soLuong;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getNgayXuat() {
        return ngayXuat;
    }

    public void setNgayXuat(Date ngayXuat) {
        this.ngayXuat = ngayXuat;
    }

    public NhanVienEntity getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVienEntity nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhoEntity getKho() {
        return kho;
    }

    public void setKho(KhoEntity kho) {
        this.kho = kho;
    }

    public List<NguyenLieuEntity> getListNguyenLieu() {
        return listNguyenLieu;
    }

    public void setListNguyenLieu(List<NguyenLieuEntity> listNguyenLieu) {
        this.listNguyenLieu = listNguyenLieu;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Long soLuong) {
        this.soLuong = soLuong;
    }
}

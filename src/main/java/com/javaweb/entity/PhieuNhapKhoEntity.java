package com.javaweb.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PhieuNhapKho")
public class PhieuNhapKhoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NgayNhap")
    private Date ngayNhap;

    @ManyToOne
    @JoinColumn(name = "Id_NhanVien")
    private NhanVienEntity nhanVien;

    @ManyToOne
    @JoinColumn(name = "Id_NhaCungCap")
    private NhaCungCapEntity nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "Id_Kho")
    private KhoEntity kho;

    @OneToMany(mappedBy = "phieuNhap")
    private List<NguyenLieuEntity> listNguyenLieu;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GiaTien")
    private Long giaTien;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public NhanVienEntity getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVienEntity nhanVien) {
        this.nhanVien = nhanVien;
    }

    public NhaCungCapEntity getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCapEntity nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
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

    public Long getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(Long giaTien) {
        this.giaTien = giaTien;
    }
}

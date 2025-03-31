package com.javaweb.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "HoaDon")
public class HoaDonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NgayGioLapHoaDon")
    private Date ngayGioLapHoaDon;

    @Column(name = "GiamGia")
    private Long giamGia;

    @Column(name = "PhuongThucThanhToan")
    private String phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "Id_ban")
    private TableEntity ban;

    @ManyToOne
    @JoinColumn(name = "Id_KhachHang")
    private KhachHangEntity khachHang;

    @ManyToOne
    @JoinColumn(name = "Id_NhanVien")
    private NhanVienEntity nhanVien;

    @OneToMany(mappedBy = "hoaDon")
    private List<ChiTietHoaDonEntity> listChiTietHoaDon;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getNgayGioLapHoaDon() {
        return ngayGioLapHoaDon;
    }

    public void setNgayGioLapHoaDon(Date ngayGioLapHoaDon) {
        this.ngayGioLapHoaDon = ngayGioLapHoaDon;
    }

    public Long getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(Long giamGia) {
        this.giamGia = giamGia;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public TableEntity getBan() {
        return ban;
    }

    public void setBan(TableEntity ban) {
        this.ban = ban;
    }

    public KhachHangEntity getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHangEntity khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVienEntity getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVienEntity nhanVien) {
        this.nhanVien = nhanVien;
    }

    public List<ChiTietHoaDonEntity> getListChiTietHoaDon() {
        return listChiTietHoaDon;
    }

    public void setListChiTietHoaDon(List<ChiTietHoaDonEntity> listChiTietHoaDon) {
        this.listChiTietHoaDon = listChiTietHoaDon;
    }
}

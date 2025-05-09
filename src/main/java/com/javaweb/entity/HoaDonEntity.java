package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
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

    @Column(name = "TrangThai")
    private String trangThai;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "PhiShip")
    private Long phiShip;

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
}

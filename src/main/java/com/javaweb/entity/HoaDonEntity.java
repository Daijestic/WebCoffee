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
    @Column(name = "ID_HoaDon")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idHoaDon;

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

    @Column(name="HinhThuc")
    private String hinhThuc;

    @Column(name="DiemDaDung")
    private Long diemDaDung;

    @ManyToOne
    @JoinColumn(name="ID_User")
    private UserEntity user;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ChiTietHoaDonEntity> chiTietHoaDons;
}

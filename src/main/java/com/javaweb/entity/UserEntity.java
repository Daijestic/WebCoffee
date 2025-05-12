package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name= "Users")
public class UserEntity {

    @Id
    @Column(name = "ID_User")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long idUser;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "GioiTinh")
    private String gioiTinh;

    @Column(name= "SDT")
    private String sdt;

    @Column(name="DiaChi")
    private String diaChi;

    @Column(name="Email")
    private String email;

    @Column(name="DangNhap", unique = true)
    private String dangNhap;

    @Column(name="MatKhau")
    private String matKhau;


    @Column(name = "LoaiUser")
    private String loaiUser;


    @Column(name="DiemTichLuy")
    private Long diemTichLuy;

    @Column(name="TongDiemTichLuy")
    private Long tongDiemTichLuy;

    @Column(name="NgayTao")
    private Date ngayTao;

    @Column(name="NgayCapNhat")
    private Date ngayCapNhat;

    @Column(name = "avatar")
    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<HoaDonEntity> hoaDons;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LichLamEntity> lichLams;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhieuNhapKhoEntity> phieuNhaps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhieuXuatKhoEntity> phieuXuats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChiTietGioHangEntity> chiTietGioHangEntityList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<NhanXetEntity> nhanXets;

}

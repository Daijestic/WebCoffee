package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "KhachHang")
public class KhachHangEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "GioiTinh")
    private String gioiTinh;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "TaiKhoan")
    private TaiKhoanEntity taiKhoan;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "theTichDiem_id", referencedColumnName = "id")
    private TheTichDiemEntity theTichDiem;

    @OneToMany(mappedBy = "khachHang")
    private List<HoaDonEntity> hoaDon;

}

package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "Id_PhieuNhapKho")
    private PhieuNhapKhoEntity phieuNhap;

    @ManyToOne
    @JoinColumn(name = "Id_PhieuXuatKho")
    private PhieuXuatKhoEntity phieuXuat;
}

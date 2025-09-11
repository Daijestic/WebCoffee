package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "NguyenLieu")
public class NguyenLieuEntity {
    @Id
    @Column(name = "ID_NguyenLieu")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNguyenLieu;

    @Column(name = "TenNguyenLieu")
    private String tenNguyenLieu;

    @Column(name = "DonViTinh")
    private String donViTinh;

    @Column(name = "SoLuong")
    private Long soLuong;

    @OneToMany(mappedBy = "idNguyenLieu", cascade = CascadeType.ALL)
    private List<ChiTietNhapKhoEntity> chiTietNhapKhoEntities;

    @OneToMany(mappedBy = "nguyenLieu", cascade = CascadeType.ALL)
    private List<ChiTietXuatKhoEntity> chiTietXuatKhoEntities;
}

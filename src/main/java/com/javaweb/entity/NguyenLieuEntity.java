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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID_NguyenLieu;

    @Column(name = "TenNguyenLieu")
    private String tenNguyenLieu;

    @Column(name = "DonViTinh")
    private String donViTinh;

    @OneToMany(mappedBy = "nguyenLieu", cascade = CascadeType.ALL)
    private List<ChiTietNhapKhoEntity> chiTietNhapKhoEntities;

    @OneToMany(mappedBy = "nguyenLieu", cascade = CascadeType.ALL)
    private List<ChiTietXuatKhoEntity> chiTietXuatKhoEntities;
}

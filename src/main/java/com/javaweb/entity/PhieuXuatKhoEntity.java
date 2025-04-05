package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
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

}

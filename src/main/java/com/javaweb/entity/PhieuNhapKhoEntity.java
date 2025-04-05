package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
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

}

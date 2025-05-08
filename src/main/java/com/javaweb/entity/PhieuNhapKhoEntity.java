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
    private Long ID_PhieuNhap;

    @Column(name = "NgayNhap")
    private Date ngayNhap;

    @ManyToOne
    @JoinColumn(name = "ID_User")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="ID_NhaCungCap")
    private NhaCungCapEntity nhaCungCap;

    @OneToMany(mappedBy = "phieuNhapKho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietNhapKhoEntity> chiTietNhapKhoList;
}
